/**
 * Copyright 2020 - 2022 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.epam.drill.compatibility.matrix

import java.net.URI
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.reactive.socket.client.WebSocketClient
import org.springframework.web.socket.config.annotation.EnableWebSocket
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import com.epam.drill.agent.instrument.TestRequestHolder
import com.epam.drill.common.agent.request.DrillRequest

@RunWith(SpringRunner::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [SpringWebfluxWebSocketMessagesMatrixTest.TestWebSocketServerConfig::class]
)
abstract class SpringWebfluxWebSocketMessagesMatrixTest : SpringCommonWebSocketMessagesMatrixTest() {

    @Autowired
    lateinit var webSocketClient: WebSocketClient

    override fun callWebSocketEndpoint(payloadType: String, body: String, count: Int) = TestWebSocketClientHandler().run {
        webSocketClient.execute(URI("ws://localhost:$serverPort"), this).subscribe()
        while (this.session?.isOpen != true) Thread.sleep(100)
        (0 until count).map(body::plus).forEach {
            when (payloadType) {
                "text" -> this.sendingTexts.tryEmitNext(it)
                "binary" -> this.sendingBinaries.tryEmitNext(it.encodeToByteArray())
            }
        }
        Thread.sleep(2000)
        this.session!!.close().block()
        this.incomingMessages to this.incomingContexts
    }

    @Configuration
    @EnableWebSocket
    @EnableAutoConfiguration
    open class TestWebSocketServerConfig {
        @Bean
        open fun testWebSocketHandler() = TestWebSocketServerHandler()
        @Bean
        open fun handlerMapping() = SimpleUrlHandlerMapping(mapOf("/" to testWebSocketHandler()), -1)
    }

    @Configuration
    abstract class AbstractTestWebSocketClientConfig {
        @Bean
        abstract fun testWebSocketClient(): WebSocketClient
    }

    open class TestWebSocketServerHandler : WebSocketHandler, TestRequestEndpoint {
        override val incomingMessages = mutableListOf<String>()
        override val incomingContexts = mutableListOf<DrillRequest?>()
        override fun handle(session: WebSocketSession): Mono<Void> = session.receive()
            .map(WebSocketMessage::getPayloadAsText)
            .doOnNext(incomingMessages::add)
            .doOnNext { incomingContexts.add(TestRequestHolder.retrieve()) }
            .map { "$it-response" }
            .doOnNext { TestRequestHolder.store(DrillRequest("$it-session", mapOf("drill-data" to "$it-data"))) }
            .map(session::textMessage)
            .let(session::send)
    }

    private class TestWebSocketClientHandler : WebSocketHandler, TestRequestEndpoint {
        override val incomingMessages = mutableListOf<String>()
        override val incomingContexts = mutableListOf<DrillRequest?>()
        val sendingTexts: Sinks.Many<String> = Sinks.many().unicast().onBackpressureBuffer()
        val sendingBinaries: Sinks.Many<ByteArray> = Sinks.many().unicast().onBackpressureBuffer()
        var session: WebSocketSession? = null
        override fun handle(session: WebSocketSession): Mono<Void> {
            this.session = session
            val input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(incomingMessages::add)
                .doOnNext { incomingContexts.add(TestRequestHolder.retrieve()) }
                .then()
            val outputTexts = sendingTexts.asFlux().map(session::textMessage)
            val outputBinaries = sendingBinaries.asFlux().map { session.binaryMessage { factory -> factory.wrap(it) } }
            val storeDrillRequest: (WebSocketMessage) -> Unit = {
                TestRequestHolder.store(
                    DrillRequest("${it.payloadAsText}-session", mapOf("drill-data" to "${it.payloadAsText}-data"))
                )
            }
            val output = Flux.merge(outputTexts, outputBinaries).doOnNext(storeDrillRequest).let(session::send)
            return Mono.zip(input, output).then()
        }
    }

}
