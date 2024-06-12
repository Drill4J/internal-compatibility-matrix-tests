package com.epam.drill.compatibility.matrix

import kotlin.test.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import reactor.core.publisher.Mono

@RunWith(SpringRunner::class)
@Suppress("FunctionName")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [SpringWebfluxWebSocketServerMatrixTest.TestWebSocketConfig::class]
)
abstract class SpringWebfluxWebSocketServerMatrixTest : AbstractWebSocketServerTest() {

    @Value("\${local.server.port}")
    lateinit var serverPort: String

    @Test
    fun `test with empty headers request`() = testEmptyHeadersRequest("ws://localhost:$serverPort", "text")

    @Test
    fun `test with session headers request`() = testSessionHeadersRequest("ws://localhost:$serverPort", "text")

    open class TestWebSocketHandler : WebSocketHandler {
        @Suppress("ReactiveStreamsTooLongSameOperatorsChain")
        override fun handle(session: WebSocketSession): Mono<Void> = session.receive()
            .map(WebSocketMessage::getPayloadAsText)
            .map(AbstractWebSocketServerTest::attachSessionHeaders)
            .map(session::textMessage)
            .let(session::send)
    }

    @Configuration
    @EnableWebSocket
    @EnableAutoConfiguration
    open class TestWebSocketConfig {
        @Bean
        open fun handlerMapping() = SimpleUrlHandlerMapping(mapOf("/" to TestWebSocketHandler()), -1)
    }

}
