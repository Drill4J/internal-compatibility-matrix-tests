package com.epam.drill.compatibility.matrix

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import java.io.Closeable
import java.nio.ByteBuffer
import java.util.logging.LogManager
import javax.websocket.EndpointConfig
import javax.websocket.HandshakeResponse
import javax.websocket.OnMessage
import javax.websocket.OnOpen
import javax.websocket.Session
import javax.websocket.server.HandshakeRequest
import javax.websocket.server.ServerEndpoint
import javax.websocket.server.ServerEndpointConfig
import org.glassfish.tyrus.core.TyrusServerEndpointConfigurator
import org.glassfish.tyrus.server.Server
import com.epam.drill.agent.instrument.TestRequestHolder
import com.epam.drill.common.agent.request.DrillRequest

@Suppress("FunctionName")
abstract class AbstractWebSocketClientTest {

    @Test
    fun `test annotated endpoint text request-response with empty thread session data`() =
        testEmptySessionDataRequest(::connectToWebsocketAnnotatedEndpoint, "text")

    @Test
    fun `test interface endpoint text request-response with empty thread session data`() =
        testEmptySessionDataRequest(::connectToWebsocketInterfaceEndpoint, "text")

    @Test
    fun `test annotated endpoint text request with existing thread session data`() =
        testExistingSessionDataRequest(::connectToWebsocketAnnotatedEndpoint, "text")

    @Test
    fun `test interface endpoint text request with existing thread session data`() =
        testExistingSessionDataRequest(::connectToWebsocketInterfaceEndpoint, "text")

    @Test
    fun `test annotated endpoint binary request-response with empty thread session data`() =
        testEmptySessionDataRequest(::connectToWebsocketAnnotatedEndpoint, "binary")

    @Test
    fun `test interface endpoint binary request-response with empty thread session data`() =
        testEmptySessionDataRequest(::connectToWebsocketInterfaceEndpoint, "binary")

    @Test
    fun `test annotated endpoint binary request with existing thread session data`() =
        testExistingSessionDataRequest(::connectToWebsocketAnnotatedEndpoint, "binary")

    @Test
    fun `test interface endpoint binary request with existing thread session data`() =
        testExistingSessionDataRequest(::connectToWebsocketInterfaceEndpoint, "binary")

    protected abstract fun connectToWebsocketAnnotatedEndpoint(endpoint: String): Pair<TestRequestClientEndpoint, Closeable>

    protected abstract fun connectToWebsocketInterfaceEndpoint(endpoint: String): Pair<TestRequestClientEndpoint, Closeable>

    protected abstract fun callWebSocketEndpoint(
        endpoint: String,
        connect: (String) -> Pair<TestRequestClientEndpoint, Closeable>,
        body: String = "test-request",
        type: String = "text"
    ): Pair<List<String>, Map<String, String>>

    private fun testEmptySessionDataRequest(
        connect: (String) -> Pair<TestRequestClientEndpoint, Closeable>,
        type: String
    ) = withWebSocketServer {
        TestRequestHolder.remove()
        val call = callWebSocketEndpoint(it, connect, type = type)
        val incomingMessages = call.first
        val handshakeHeaders = call.second
        assertNull(TestRequestHolder.retrieve())
        assertNull(handshakeHeaders["drill-session-id"])
        assertNull(handshakeHeaders["drill-header-data"])
        assertEquals(2, incomingMessages.size)
        assertEquals("test-request", incomingMessages[1])
    }

    private fun testExistingSessionDataRequest(
        connect: (String) -> Pair<TestRequestClientEndpoint, Closeable>,
        type: String
    ) = withWebSocketServer {
        TestRequestHolder.store(DrillRequest("session-123", mapOf("drill-header-data" to "test-data")))
        val call = callWebSocketEndpoint(it, connect, type = type)
        val incomingMessages = call.first
        val handshakeHeaders = call.second
        assertEquals("session-123", handshakeHeaders["drill-session-id"])
        assertEquals("test-data", handshakeHeaders["drill-header-data"])
        assertEquals(2, incomingMessages.size)
        assertEquals("test-request", incomingMessages[1])
    }

    private fun withWebSocketServer(block: (String) -> Unit) = Server(TestRequestServerEndpoint::class.java).run {
        try {
            LogManager.getLogManager().readConfiguration(ClassLoader.getSystemResourceAsStream("logging.properties"))
            this.start()
            block("ws://localhost:${this.port}")
        } finally {
            this.stop()
        }
    }

    @Suppress("unused")
    @ServerEndpoint(value = "/", configurator = TestRequestConfigurator::class)
    class TestRequestServerEndpoint {
        @OnOpen
        fun onOpen(session: Session, config: EndpointConfig) = config
            .let { it as ServerEndpointConfig }
            .let { it.configurator as TestRequestConfigurator }
            .headers
            .map { (key, value) -> "${key}=${value}" }
            .joinToString("\n", "session-headers:\n")
            .also(session.basicRemote::sendText)
        @OnMessage
        fun onTextMessage(message: String, session: Session) = session.basicRemote.sendText(message)
        @OnMessage
        fun onBinaryMessage(message: ByteBuffer, session: Session) = session.basicRemote.sendBinary(message)
    }

    class TestRequestConfigurator : TyrusServerEndpointConfigurator() {
        val headers = mutableMapOf<String, String>()
        override fun modifyHandshake(sec: ServerEndpointConfig, req: HandshakeRequest, resp: HandshakeResponse) =
            req.headers.forEach { headers[it.key] = it.value.joinToString(",") }
    }

    protected interface TestRequestClientEndpoint {
        val incomingMessages: MutableList<String>
    }

}
