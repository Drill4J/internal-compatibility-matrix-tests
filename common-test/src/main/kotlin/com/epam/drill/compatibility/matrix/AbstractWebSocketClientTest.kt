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
import kotlin.test.assertEquals
import kotlin.test.assertNull
import java.io.Closeable
import com.epam.drill.agent.instrument.TestRequestHolder
import com.epam.drill.common.agent.request.DrillRequest

@Suppress("FunctionName")
abstract class AbstractWebSocketClientTest : AbstractTestServerWebSocketTest() {

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

    protected interface TestRequestClientEndpoint {
        val incomingMessages: MutableList<String>
    }

}
