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

import kotlin.test.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.handler.TextWebSocketHandler

@RunWith(SpringRunner::class)
@Suppress("FunctionName")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [SpringMVCWebSocketServerMatrixTest.TestWebSocketConfig::class]
)
abstract class SpringMVCWebSocketServerMatrixTest : AbstractWebSocketServerTest() {

    @Value("\${local.server.port}")
    lateinit var serverPort: String

    @Test
    fun `test with empty headers request`() = testEmptyHeadersRequest("ws://localhost:$serverPort", "text")

    @Test
    fun `test with session headers request`() = testSessionHeadersRequest("ws://localhost:$serverPort", "text")

    open class TestWebSocketHandler : TextWebSocketHandler() {
        override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
            session.sendMessage(TextMessage(attachSessionHeaders(message.payload)))
        }
    }

    @Configuration
    @EnableWebSocket
    @EnableAutoConfiguration
    open class TestWebSocketConfig : WebSocketConfigurer {
        override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
            registry.addHandler(testWebSocketHandler(), "/")
        }
        @Bean
        open fun testWebSocketHandler() = TestWebSocketHandler()
    }

}
