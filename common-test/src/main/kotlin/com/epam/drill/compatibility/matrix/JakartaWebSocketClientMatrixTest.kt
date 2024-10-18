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

import java.io.Closeable
import java.nio.ByteBuffer
import jakarta.websocket.ClientEndpoint
import jakarta.websocket.Endpoint
import jakarta.websocket.EndpointConfig
import jakarta.websocket.MessageHandler
import jakarta.websocket.OnMessage
import jakarta.websocket.Session

abstract class JakartaWebSocketClientMatrixTest : AbstractWebSocketClientTest() {

    override fun callWebSocketEndpoint(
        endpoint: String,
        connect: (String) -> Pair<TestRequestClientEndpoint, Closeable>,
        body: String,
        type: String
    ) = connect(endpoint).run {
        val incomingMessages = this.first.incomingMessages
        val session = this.second as Session
        when(type) {
            "text" -> session.basicRemote.sendText(body)
            "binary" -> session.basicRemote.sendBinary(ByteBuffer.wrap(body.encodeToByteArray()))
        }
        Thread.sleep(500)
        session.close()
        val handshakeHeaders = incomingMessages[0].removePrefix("session-headers:\n").lines()
            .associate { it.substringBefore("=") to it.substringAfter("=", "") }
        incomingMessages to handshakeHeaders
    }

    @Suppress("unused")
    @ClientEndpoint
    protected class TestRequestAnnotatedClientEndpoint : TestRequestClientEndpoint {
        override val incomingMessages = mutableListOf<String>()
        @OnMessage
        fun onTextMessage(message: String) {
            incomingMessages.add(message)
        }
        @OnMessage
        fun onBinaryMessage(message: ByteBuffer) {
            incomingMessages.add(ByteArray(message.limit()).also(message::get).decodeToString())
        }
    }

    protected class TestRequestInterfaceClientEndpoint : Endpoint(), TestRequestClientEndpoint {
        override val incomingMessages = mutableListOf<String>()
        override fun onOpen(session: Session, config: EndpointConfig) {
            session.addMessageHandler(TextMessageHandler(incomingMessages))
            session.addMessageHandler(BinaryMessageHandler(incomingMessages))
        }
    }

    private class TextMessageHandler(private val incomingMessages: MutableList<String>) : MessageHandler.Whole<String> {
        override fun onMessage(message: String) {
            incomingMessages.add(message)
        }
    }

    private class BinaryMessageHandler(private val incomingMessages: MutableList<String>) : MessageHandler.Whole<ByteBuffer> {
        override fun onMessage(message: ByteBuffer) {
            incomingMessages.add(ByteArray(message.limit()).also(message::get).decodeToString())
        }
    }

}
