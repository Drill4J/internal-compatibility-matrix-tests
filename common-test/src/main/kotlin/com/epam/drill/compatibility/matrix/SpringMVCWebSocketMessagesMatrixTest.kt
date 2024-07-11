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

import org.junit.runner.RunWith
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.socket.BinaryMessage
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.WebSocketClient
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.handler.AbstractWebSocketHandler
import com.epam.drill.agent.instrument.TestRequestHolder
import com.epam.drill.common.agent.request.DrillRequest

@RunWith(SpringRunner::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [SpringMVCWebSocketMessagesMatrixTest.TestWebSocketServerConfig::class]
)
abstract class SpringMVCWebSocketMessagesMatrixTest : SpringCommonWebSocketMessagesMatrixTest() {

    @Autowired
    lateinit var beanFactory: BeanFactory

    override fun callWebSocketEndpoint(payloadType: String, body: String, count: Int) = TestWebSocketClientHandler().run {
        val session = beanFactory.getBean(WebSocketSession::class.java, "ws://localhost:$serverPort", this)
        (0 until count).map(body::plus).forEach {
            TestRequestHolder.store(DrillRequest("$it-session", mapOf("drill-data" to "$it-data")))
            when (payloadType) {
                "text" -> session.sendMessage(TextMessage(it))
                "binary" -> session.sendMessage(BinaryMessage(it.encodeToByteArray()))
            }
            TestRequestHolder.remove()
        }
        Thread.sleep(1000)
        session.close()
        this.incomingMessages to this.incomingContexts
    }

    @Configuration
    @EnableWebSocket
    @EnableAutoConfiguration
    open class TestWebSocketServerConfig : WebSocketConfigurer {
        override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
            registry.addHandler(testWebSocketHandler(), "/")
        }
        @Bean
        open fun testWebSocketHandler() = TestWebSocketServerHandler()
    }

    @Configuration
    abstract class AbstractTestWebSocketClientConfig {
        @Bean
        abstract fun testWebSocketClient(): WebSocketClient
        @Bean
        @Scope(BeanDefinition.SCOPE_PROTOTYPE)
        open fun testWebSocketClientSession(address: String, handler: AbstractWebSocketHandler): WebSocketSession =
            testWebSocketClient().doHandshake(handler, address).get()
    }

    open class TestWebSocketServerHandler : AbstractWebSocketHandler(), TestRequestEndpoint {
        override val incomingMessages = mutableListOf<String>()
        override val incomingContexts = mutableListOf<DrillRequest?>()
        override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
            val text = message.payload
            incomingMessages.add(text)
            incomingContexts.add(TestRequestHolder.retrieve())
            TestRequestHolder.store(DrillRequest("$text-response-session", mapOf("drill-data" to "$text-response-data")))
            session.sendMessage(TextMessage("$text-response"))
        }
        override fun handleBinaryMessage(session: WebSocketSession, message: BinaryMessage) {
            val text = ByteArray(message.payload.limit()).also(message.payload::get).decodeToString()
            incomingMessages.add(text)
            incomingContexts.add(TestRequestHolder.retrieve())
            TestRequestHolder.store(DrillRequest("$text-response-session", mapOf("drill-data" to "$text-response-data")))
            session.sendMessage(BinaryMessage("$text-response".encodeToByteArray()))
        }
    }

    private class TestWebSocketClientHandler : AbstractWebSocketHandler(), TestRequestEndpoint {
        override val incomingMessages = mutableListOf<String>()
        override val incomingContexts = mutableListOf<DrillRequest?>()
        override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
            incomingMessages.add(message.payload)
            incomingContexts.add(TestRequestHolder.retrieve())
        }
        override fun handleBinaryMessage(session: WebSocketSession, message: BinaryMessage) {
            incomingMessages.add(ByteArray(message.payload.limit()).also(message.payload::get).decodeToString())
            incomingContexts.add(TestRequestHolder.retrieve())
        }
    }

}
