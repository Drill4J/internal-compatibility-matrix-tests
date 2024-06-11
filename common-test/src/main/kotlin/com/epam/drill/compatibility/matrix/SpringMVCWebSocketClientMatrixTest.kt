package com.epam.drill.compatibility.matrix

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.runner.RunWith
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.WebSocketClient
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.handler.TextWebSocketHandler
import com.epam.drill.agent.instrument.TestRequestHolder
import com.epam.drill.common.agent.request.DrillRequest

@RunWith(SpringRunner::class)
@Suppress("FunctionName")
@ContextConfiguration(classes = [SpringMVCWebSocketClientMatrixTest.TestWebSocketClientConfig::class])
open class SpringMVCWebSocketClientMatrixTest : AbstractTestServerWebSocketTest() {

    @Autowired
    lateinit var beanFactory: BeanFactory

    @Test
    fun `test with empty headers request`() = withWebSocketServer {
        TestRequestHolder.remove()
        val call = callWebSocketEndpoint(it)
        val incomingMessages = call.first
        val handshakeHeaders = call.second
        assertNull(TestRequestHolder.retrieve())
        assertNull(handshakeHeaders["drill-session-id"])
        assertNull(handshakeHeaders["drill-header-data"])
        assertEquals(2, incomingMessages.size)
        assertEquals("test-request", incomingMessages[1])
    }

    @Test
    fun `test with session headers request`() = withWebSocketServer {
        TestRequestHolder.store(DrillRequest("session-123", mapOf("drill-header-data" to "test-data")))
        val call = callWebSocketEndpoint(it)
        val incomingMessages = call.first
        val handshakeHeaders = call.second
        assertEquals("session-123", handshakeHeaders["drill-session-id"])
        assertEquals("test-data", handshakeHeaders["drill-header-data"])
        assertEquals(2, incomingMessages.size)
        assertEquals("test-request", incomingMessages[1])
    }

    private fun callWebSocketEndpoint(
        endpoint: String,
        body: String = "test-request"
    ) = TestWebSocketHandler().run {
        val session = beanFactory.getBean(WebSocketSession::class.java, endpoint, this)
        session.sendMessage(TextMessage(body))
        Thread.sleep(500)
        session.close()
        val handshakeHeaders = incomingMessages[0].removePrefix("session-headers:\n").lines()
            .associate { it.substringBefore("=") to it.substringAfter("=", "") }
        this.incomingMessages to handshakeHeaders
    }

    @Configuration
    @EnableWebSocket
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    open class TestWebSocketClientConfig {
        @Autowired
        lateinit var webSocketClient: WebSocketClient
        @Bean
        @Scope(BeanDefinition.SCOPE_PROTOTYPE)
        open fun testWebSocketClientSession(address: String, handler: TextWebSocketHandler): WebSocketSession =
            webSocketClient.doHandshake(handler, address).get()
    }

    private class TestWebSocketHandler : TextWebSocketHandler() {
        val incomingMessages = mutableListOf<String>()
        override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
            incomingMessages.add(message.payload)
        }
    }

}
