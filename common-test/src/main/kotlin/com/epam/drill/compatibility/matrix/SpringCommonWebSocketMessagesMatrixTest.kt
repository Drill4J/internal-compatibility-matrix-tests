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
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.test.context.junit4.SpringRunner
import com.epam.drill.compatibility.context.TestRequestHolder
import com.epam.drill.compatibility.context.DrillRequest

@RunWith(SpringRunner::class)
@Suppress("FunctionName")
abstract class SpringCommonWebSocketMessagesMatrixTest {

    @Value("\${local.server.port}")
    lateinit var serverPort: String

    @Autowired
    lateinit var serverEndpoint: TestRequestEndpoint

    @Test
    fun `test with string payload`() = testPerMessageRequests("text")

    @Test
    fun `test with binary payload`() = testPerMessageRequests("binary")

    protected abstract fun callWebSocketEndpoint(
        payloadType: String,
        body: String = "test-request-",
        count: Int = 10
    ): Pair<List<String>, List<DrillRequest?>>

    private fun testPerMessageRequests(payloadType: String) {
        TestRequestHolder.remove()
        serverEndpoint.incomingMessages.clear()
        serverEndpoint.incomingContexts.clear()
        val responses = callWebSocketEndpoint(payloadType)
        assertEquals(10, serverEndpoint.incomingMessages.size)
        assertEquals(10, serverEndpoint.incomingContexts.size)
        assertEquals(10, responses.first.size)
        assertEquals(10, responses.second.size)
        serverEndpoint.incomingMessages.forEachIndexed { i, message ->
            assertEquals("test-request-$i", message)
        }
        serverEndpoint.incomingContexts.forEachIndexed { i, drillRequest ->
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
        val incomingMessages: MutableList<String>
        val incomingContexts: MutableList<DrillRequest?>
    }

}
