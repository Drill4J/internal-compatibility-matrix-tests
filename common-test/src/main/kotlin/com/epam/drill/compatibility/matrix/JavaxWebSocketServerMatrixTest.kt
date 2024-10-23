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
import java.nio.ByteBuffer
import javax.websocket.Endpoint
import javax.websocket.EndpointConfig
import javax.websocket.MessageHandler
import javax.websocket.OnMessage
import javax.websocket.Session
import javax.websocket.server.ServerEndpoint
import mu.KLogger

@Suppress("FunctionName")
abstract class JavaxWebSocketServerMatrixTest : AbstractWebSocketServerTest() {

    protected abstract val logger: KLogger

    @Test
    fun `test with empty headers text request to annotated endpoint`() =
        withWebSocketAnnotatedEndpoint(::testEmptyHeadersTextRequest)

    @Test
    fun `test with empty headers text request to interface endpoint`() =
        withWebSocketInterfaceEndpoint(::testEmptyHeadersTextRequest)

    @Test
    fun `test with session headers text request to annotated endpoint`() =
        withWebSocketAnnotatedEndpoint(::testSessionHeadersTextRequest)

    @Test
    fun `test with session headers text request to interface endpoint`() =
        withWebSocketInterfaceEndpoint(::testSessionHeadersTextRequest)

    @Test
    fun `test with empty headers binary request to annotated endpoint`() =
        withWebSocketAnnotatedEndpoint(::testEmptyHeadersBinaryRequest)

    @Test
    fun `test with empty headers binary request to interface endpoint`() =
        withWebSocketInterfaceEndpoint(::testEmptyHeadersBinaryRequest)

    @Test
    fun `test with session headers binary request to annotated endpoint`() =
        withWebSocketAnnotatedEndpoint(::testSessionHeadersBinaryRequest)

    @Test
    fun `test with session headers binary request to interface endpoint`() =
        withWebSocketInterfaceEndpoint(::testSessionHeadersBinaryRequest)

    protected abstract fun withWebSocketAnnotatedEndpoint(block: (String) -> Unit)

    protected abstract fun withWebSocketInterfaceEndpoint(block: (String) -> Unit)

    private fun testEmptyHeadersTextRequest(address: String) = testEmptyHeadersRequest(address, "text")

    private fun testSessionHeadersTextRequest(address: String) = testSessionHeadersRequest(address, "text")

    private fun testEmptyHeadersBinaryRequest(address: String) = testEmptyHeadersRequest(address, "binary")

    private fun testSessionHeadersBinaryRequest(address: String) = testSessionHeadersRequest(address, "binary")

    @ServerEndpoint(value = "/")
    @Suppress("unused")
    class TestRequestServerAnnotatedEndpoint {
        @OnMessage
        fun onTextMessage(message: String, session: Session) {
            session.basicRemote.sendText(attachSessionHeaders(message))
        }
        @OnMessage
        fun onBinaryMessage(message: ByteBuffer, session: Session) {
            val text = ByteArray(message.limit()).also(message::get).decodeToString()
            session.basicRemote.sendBinary(ByteBuffer.wrap(attachSessionHeaders(text).encodeToByteArray()))
        }
    }

    class TestRequestServerInterfaceEndpoint : Endpoint() {
        override fun onOpen(session: Session, config: EndpointConfig) = try {
            session.addMessageHandler(String::class.java) { message ->
                session.basicRemote.sendText(attachSessionHeaders(message))
            }
            session.addMessageHandler(ByteBuffer::class.java) { message ->
                val text = ByteArray(message.limit()).also(message::get).decodeToString()
                session.basicRemote.sendBinary(ByteBuffer.wrap(attachSessionHeaders(text).encodeToByteArray()))
            }
        } catch (e: AbstractMethodError) {
            session.addMessageHandler(TextMessageHandler(session))
            session.addMessageHandler(BinaryMessageHandler(session))
        }
    }

    private class TextMessageHandler(private val session: Session) : MessageHandler.Whole<String> {
        override fun onMessage(message: String) {
            session.basicRemote.sendText(attachSessionHeaders(message))
        }
    }

    private class BinaryMessageHandler(private val session: Session) : MessageHandler.Whole<ByteBuffer> {
        override fun onMessage(message: ByteBuffer) {
            val text = ByteArray(message.limit()).also(message::get).decodeToString()
            session.basicRemote.sendBinary(ByteBuffer.wrap(attachSessionHeaders(text).encodeToByteArray()))
        }
    }

}