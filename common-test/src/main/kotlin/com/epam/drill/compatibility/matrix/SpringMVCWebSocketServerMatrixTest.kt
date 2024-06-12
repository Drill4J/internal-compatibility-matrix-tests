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
