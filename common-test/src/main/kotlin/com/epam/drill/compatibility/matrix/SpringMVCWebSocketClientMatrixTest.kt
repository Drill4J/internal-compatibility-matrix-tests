package com.epam.drill.compatibility.matrix

import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.WebSocketClient
import org.springframework.web.socket.handler.TextWebSocketHandler

abstract class SpringMVCWebSocketClientMatrixTest : SpringCommonWebSocketClientMatrixTest() {

    @Autowired
    lateinit var beanFactory: BeanFactory

    override fun callWebSocketEndpoint(endpoint: String, body: String) = TestWebSocketHandler().run {
        val session = beanFactory.getBean(WebSocketSession::class.java, endpoint, this)
        session.sendMessage(TextMessage(body))
        Thread.sleep(500)
        session.close()
        val handshakeHeaders = this.incomingMessages[0].removePrefix("session-headers:\n").lines()
            .associate { it.substringBefore("=") to it.substringAfter("=", "") }
        this.incomingMessages to handshakeHeaders
    }

    @Configuration
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    abstract class AbstractTestWebSocketClientConfig {
        @Bean
        abstract fun testWebSocketClient(): WebSocketClient
        @Bean
        @Scope(BeanDefinition.SCOPE_PROTOTYPE)
        open fun testWebSocketClientSession(address: String, handler: TextWebSocketHandler): WebSocketSession =
            testWebSocketClient().doHandshake(handler, address).get()
    }

    private class TestWebSocketHandler : TextWebSocketHandler() {
        val incomingMessages = mutableListOf<String>()
        override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
            incomingMessages.add(message.payload)
        }
    }

}
