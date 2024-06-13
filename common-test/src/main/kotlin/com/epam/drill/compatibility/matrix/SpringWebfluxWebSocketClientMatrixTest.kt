package com.epam.drill.compatibility.matrix

import java.net.URI
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.reactive.socket.client.WebSocketClient
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks

abstract class SpringWebfluxWebSocketClientMatrixTest : SpringCommonWebSocketClientMatrixTest() {

    @Autowired
    lateinit var webSocketClient: WebSocketClient

    override fun callWebSocketEndpoint(endpoint: String, body: String) = TestWebSocketHandler().run {
        webSocketClient.execute(URI(endpoint), this).subscribe()
        while (this.session?.isOpen != true) Thread.sleep(100)
        this.sendingMessages.tryEmitNext(body)
        Thread.sleep(2000)
        this.session!!.close().block()
        val handshakeHeaders = this.incomingMessages[0].removePrefix("session-headers:\n").lines()
            .associate { it.substringBefore("=") to it.substringAfter("=", "") }
        this.incomingMessages to handshakeHeaders
    }

    @Configuration
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    abstract class AbstractTestWebSocketClientConfig {
        @Bean
        abstract fun testWebSocketClient(): WebSocketClient
    }

    private class TestWebSocketHandler : WebSocketHandler {
        val incomingMessages = mutableListOf<String>()
        val sendingMessages: Sinks.Many<String> = Sinks.many().unicast().onBackpressureBuffer()
        var session: WebSocketSession? = null
        override fun handle(session: WebSocketSession): Mono<Void> {
            this.session = session
            val input = session.receive().map(WebSocketMessage::getPayloadAsText).map(incomingMessages::add).then()
            val output = session.send(sendingMessages.asFlux().map(session::textMessage))
            return Mono.zip(input, output).then()
        }
    }

}
