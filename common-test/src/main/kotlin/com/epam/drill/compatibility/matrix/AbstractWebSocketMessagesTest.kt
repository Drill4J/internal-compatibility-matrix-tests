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
import kotlin.test.assertNotNull
import java.io.Closeable
import com.epam.drill.agent.instrument.TestRequestHolder
import com.epam.drill.common.agent.request.DrillRequest

@Suppress("FunctionName")
abstract class AbstractWebSocketMessagesTest {

    @Test
    fun `test annotated endpoint sync-remote string payload`() = testPerMessageRequests(
        ::withWebSocketServerAnnotatedEndpoint,
        ::connectToWebsocketAnnotatedEndpoint,
        "text", "basic"
    )

    @Test
    fun `test annotated endpoint sync-remote binary payload`() = testPerMessageRequests(
        ::withWebSocketServerAnnotatedEndpoint,
        ::connectToWebsocketAnnotatedEndpoint,
        "binary", "basic"
    )

    @Test
    fun `test annotated endpoint async-remote string payload`() = testPerMessageRequests(
        ::withWebSocketServerAnnotatedEndpoint,
        ::connectToWebsocketAnnotatedEndpoint,
        "text", "async"
    )

    @Test
    fun `test annotated endpoint async-remote binary payload`() = testPerMessageRequests(
        ::withWebSocketServerAnnotatedEndpoint,
        ::connectToWebsocketAnnotatedEndpoint,
        "binary", "async"
    )

    @Test
    fun `test interface endpoint sync-remote string payload`() = testPerMessageRequests(
        ::withWebSocketServerInterfaceEndpoint,
        ::connectToWebsocketInterfaceEndpoint,
        "text", "basic"
    )

    @Test
    fun `test interface endpoint sync-remote binary payload`() = testPerMessageRequests(
        ::withWebSocketServerInterfaceEndpoint,
        ::connectToWebsocketInterfaceEndpoint,
        "binary", "basic"
    )

    @Test
    fun `test interface endpoint async-remote string payload`() = testPerMessageRequests(
        ::withWebSocketServerInterfaceEndpoint,
        ::connectToWebsocketInterfaceEndpoint,
        "text", "async"
    )

    @Test
    fun `test interface endpoint async-remote binary payload`() = testPerMessageRequests(
        ::withWebSocketServerInterfaceEndpoint,
        ::connectToWebsocketInterfaceEndpoint,
        "binary", "async"
    )

    protected abstract fun withWebSocketServerAnnotatedEndpoint(block: (String) -> Unit)

    protected abstract fun withWebSocketServerInterfaceEndpoint(block: (String) -> Unit)

    protected abstract fun connectToWebsocketAnnotatedEndpoint(address: String): Pair<TestRequestEndpoint, Closeable>

    protected abstract  fun connectToWebsocketInterfaceEndpoint(address: String): Pair<TestRequestEndpoint, Closeable>

    protected abstract fun callWebSocketEndpoint(
        withConnection: (String) -> Pair<TestRequestEndpoint, Closeable>,
        address: String,
        payloadType: String,
        sendType: String,
        body: String = "test-request-",
        count: Int = 10
    ): Pair<List<String>, List<DrillRequest?>>

    private fun testPerMessageRequests(
        withServer: (block: (String) -> Unit) -> Unit,
        withConnection: (String) -> Pair<TestRequestEndpoint, Closeable>,
        payloadType: String,
        sendType: String
    ) = withServer { address ->
        TestRequestEndpoint.incomingMessages.clear()
        TestRequestEndpoint.incomingContexts.clear()
        TestRequestHolder.remove()
        val responses = callWebSocketEndpoint(withConnection, address, payloadType, sendType)
        assertEquals(10, TestRequestEndpoint.incomingMessages.size)
        assertEquals(10, TestRequestEndpoint.incomingContexts.size)
        assertEquals(10, responses.first.size)
        assertEquals(10, responses.second.size)
        TestRequestEndpoint.incomingMessages.forEachIndexed { i, message ->
            assertEquals("test-request-$i", message)
        }
        TestRequestEndpoint.incomingContexts.forEachIndexed { i, drillRequest ->
            assertNotNull(drillRequest)
            assertEquals("test-request-$i-session", drillRequest.drillSessionId)
            assertEquals("test-request-$i-data", drillRequest.headers["drill-data"])
        }
        responses.first.forEachIndexed { i, message ->
            assertEquals("test-request-$i-response", message)
        }
        responses.second.forEachIndexed { i, drillRequest ->
            assertNotNull(drillRequest)
            assertEquals("test-request-$i-response-session", drillRequest.drillSessionId)
            assertEquals("test-request-$i-response-data", drillRequest.headers["drill-data"])
        }
    }

    interface TestRequestEndpoint {
        companion object {
            val incomingMessages = mutableListOf<String>()
            val incomingContexts = mutableListOf<DrillRequest?>()
        }
        val incomingMessages: MutableList<String>
        val incomingContexts: MutableList<DrillRequest?>
    }

}
