package com.epam.drill.compatibility.matrix

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringRunner
import com.epam.drill.agent.instrument.TestRequestHolder
import com.epam.drill.common.agent.request.DrillRequest

@RunWith(SpringRunner::class)
@Suppress("FunctionName")
abstract class SpringCommonWebSocketClientMatrixTest : AbstractTestServerWebSocketTest() {

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

    protected abstract fun callWebSocketEndpoint(
        endpoint: String,
        body: String = "test-request"
    ): Pair<List<String>, Map<String, String>>

}
