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
import javax.websocket.ClientEndpoint
import javax.websocket.Endpoint
import javax.websocket.EndpointConfig
import javax.websocket.MessageHandler
import javax.websocket.OnMessage
import javax.websocket.Session
import javax.websocket.server.ServerEndpoint
import com.epam.drill.agent.instrument.TestRequestHolder
import com.epam.drill.compatibility.context.DrillRequest

abstract class JavaxWebSocketMessagesMatrixTest : AbstractWebSocketMessagesTest() {

    override fun callWebSocketEndpoint(
        withConnection: (String) -> Pair<TestRequestEndpoint, Closeable>,
        address: String,
        payloadType: String,
        sendType: String,
        body: String,
        count: Int
    ) = withConnection(address).run {
        val session = this.second as Session
        when (payloadType) {
            "text" -> when (sendType) {
                "basic" -> (0 until count).map(body::plus).forEach {
                    TestRequestHolder.store(DrillRequest("$it-session", mapOf("drill-data" to "$it-data")))
                    session.basicRemote.sendText(it)
                    TestRequestHolder.remove()
                }
                "async" -> (0 until count).map(body::plus).forEach {
                    TestRequestHolder.store(DrillRequest("$it-session", mapOf("drill-data" to "$it-data")))
                    session.asyncRemote.sendText(it)
                    TestRequestHolder.remove()
                }
            }
            "binary" -> when (sendType) {
                "basic" -> (0 until count).map(body::plus).forEach {
                    TestRequestHolder.store(DrillRequest("$it-session", mapOf("drill-data" to "$it-data")))
                    session.basicRemote.sendBinary(ByteBuffer.wrap(it.encodeToByteArray()))
                    TestRequestHolder.remove()
                }
                "async" -> (0 until count).map(body::plus).forEach {
                    TestRequestHolder.store(DrillRequest("$it-session", mapOf("drill-data" to "$it-data")))
                    session.asyncRemote.sendBinary(ByteBuffer.wrap(it.encodeToByteArray()))
                    TestRequestHolder.remove()
                }
            }
        }
        Thread.sleep(1000)
        session.close()
        this.first.incomingMessages to this.first.incomingContexts
    }

    interface JavaxTestRequestEndpoint : TestRequestEndpoint {
        fun processIncoming(message: String, session: Session?) {
            incomingMessages.add(message)
            incomingContexts.add(TestRequestHolder.retrieve())
            if (session != null) {
                TestRequestHolder.store(DrillRequest("$message-response-session", mapOf("drill-data" to "$message-response-data")))
                session.basicRemote.sendText("$message-response")
                TestRequestHolder.remove()
            }
        }
        fun processIncoming(message: ByteBuffer, session: Session?) {
            val text = ByteArray(message.limit()).also(message::get).decodeToString()
            incomingMessages.add(text)
            incomingContexts.add(TestRequestHolder.retrieve())
            if (session != null) {
                TestRequestHolder.store(DrillRequest("$text-response-session", mapOf("drill-data" to "$text-response-data")))
                session.basicRemote.sendBinary(ByteBuffer.wrap("$text-response".encodeToByteArray()))
                TestRequestHolder.remove()
            }
        }
    }

    @Suppress("unused")
    @ServerEndpoint(value = "/")
    class TestRequestServerAnnotatedEndpoint : JavaxTestRequestEndpoint {
        override val incomingMessages = TestRequestEndpoint.incomingMessages
        override val incomingContexts = TestRequestEndpoint.incomingContexts
        @OnMessage
        fun onTextMessage(message: String, session: Session) = processIncoming(message, session)
        @OnMessage
        fun onBinaryMessage(message: ByteBuffer, session: Session) = processIncoming(message, session)
    }

    class TestRequestServerInterfaceEndpoint : Endpoint(), JavaxTestRequestEndpoint {
        override val incomingMessages = TestRequestEndpoint.incomingMessages
        override val incomingContexts = TestRequestEndpoint.incomingContexts
        override fun onOpen(session: Session, config: EndpointConfig) = try {
            session.addMessageHandler(String::class.java) { message -> processIncoming(message, session) }
            session.addMessageHandler(ByteBuffer::class.java) { message -> processIncoming(message, session) }
        } catch (e: AbstractMethodError) {
            session.addMessageHandler(ServerTextMessageHandler(session, incomingMessages, incomingContexts))
            session.addMessageHandler(ServerBinaryMessageHandler(session, incomingMessages, incomingContexts))
        }
    }

    @Suppress("unused")
    @ClientEndpoint
    class TestRequestClientAnnotatedEndpoint : JavaxTestRequestEndpoint {
        override val incomingMessages = mutableListOf<String>()
        override val incomingContexts = mutableListOf<DrillRequest?>()
        @OnMessage
        fun onTextMessage(message: String) = processIncoming(message, null)
        @OnMessage
        fun onBinaryMessage(message: ByteBuffer) = processIncoming(message, null)
    }

    class TestRequestClientInterfaceEndpoint : Endpoint(), JavaxTestRequestEndpoint {
        override val incomingMessages = mutableListOf<String>()
        override val incomingContexts = mutableListOf<DrillRequest?>()
        override fun onOpen(session: Session, config: EndpointConfig) = try {
            session.addMessageHandler(String::class.java) { message -> processIncoming(message, null) }
            session.addMessageHandler(ByteBuffer::class.java) { message -> processIncoming(message, null) }
        } catch (e: AbstractMethodError) {
            session.addMessageHandler(ClientTextMessageHandler(incomingMessages, incomingContexts))
            session.addMessageHandler(ClientBinaryMessageHandler(incomingMessages, incomingContexts))
        }
    }

    private class ServerTextMessageHandler(
        private val session: Session,
        override val incomingMessages: MutableList<String>,
        override val incomingContexts: MutableList<DrillRequest?>
    ) : MessageHandler.Whole<String>, JavaxTestRequestEndpoint {
        override fun onMessage(message: String) = processIncoming(message, session)
    }

    private class ServerBinaryMessageHandler(
        private val session: Session,
        override val incomingMessages: MutableList<String>,
        override val incomingContexts: MutableList<DrillRequest?>
    ) : MessageHandler.Whole<ByteBuffer>, JavaxTestRequestEndpoint {
        override fun onMessage(message: ByteBuffer) = processIncoming(message, session)
    }

    private class ClientTextMessageHandler(
        override val incomingMessages: MutableList<String>,
        override val incomingContexts: MutableList<DrillRequest?>
    ) : MessageHandler.Whole<String>, JavaxTestRequestEndpoint {
        override fun onMessage(message: String) = processIncoming(message, null)
    }

    private class ClientBinaryMessageHandler(
        override val incomingMessages: MutableList<String>,
        override val incomingContexts: MutableList<DrillRequest?>
    ) : MessageHandler.Whole<ByteBuffer>, JavaxTestRequestEndpoint {
        override fun onMessage(message: ByteBuffer) = processIncoming(message, null)
    }

}
