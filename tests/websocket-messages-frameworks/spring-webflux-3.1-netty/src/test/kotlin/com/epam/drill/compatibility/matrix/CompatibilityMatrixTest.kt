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
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import org.springframework.web.reactive.socket.client.WebSocketClient
import com.epam.drill.agent.instrument.TestPayloadProcessor
import com.epam.drill.agent.instrument.TestRequestHolder
import com.epam.drill.common.agent.request.DrillRequest

@ContextConfiguration(classes = [CompatibilityMatrixTest.TestWebSocketClientConfig::class])
class CompatibilityMatrixTest : SpringWebfluxWebSocketMessagesMatrixTest() {

    override fun callWebSocketEndpoint(payloadType: String, body: String, count: Int) = TestWebSocketClientHandler().run {
        webSocketClient.execute(URI("ws://localhost:$serverPort"), this).subscribe()
        while (this.session?.isOpen != true) Thread.sleep(100)
        (0 until count).map(body::plus).forEach {
            TestRequestHolder.store(DrillRequest("$it-session", mapOf("drill-data" to "$it-data")))
            val msg = TestPayloadProcessor.storeDrillHeaders(it)!!
            // TODO: Intersection of Netty per-message transformer and Reactor transformer should be additionally investigated
            // Manually injected drill payload is used here, as chosen approach to ws-messages emitting (via sink) isn't
            // work with Netty per-message transformer.
            // However transformer works at Netty server side where another approach is used (w/o sinks).
            when (payloadType) {
                "text" -> this.sendingEmitter.next(msg)
                "binary" -> this.sendingEmitter.next(msg.encodeToByteArray())
            }
            Thread.sleep(100)
            TestRequestHolder.remove()
        }
        Thread.sleep(2000)
        this.session!!.close().block()
        this.incomingMessages to this.incomingContexts
    }

    @Configuration
    open class TestWebSocketClientConfig: AbstractTestWebSocketClientConfig() {
        @Bean
        override fun testWebSocketClient(): WebSocketClient = ReactorNettyWebSocketClient()
    }

}
