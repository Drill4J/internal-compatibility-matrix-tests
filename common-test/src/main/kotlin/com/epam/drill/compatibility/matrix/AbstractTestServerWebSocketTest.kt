package com.epam.drill.compatibility.matrix

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

abstract class AbstractTestServerWebSocketTest {

    protected fun withWebSocketServer(block: (String) -> Unit) = Server(TestRequestServerEndpoint::class.java).run {
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

}