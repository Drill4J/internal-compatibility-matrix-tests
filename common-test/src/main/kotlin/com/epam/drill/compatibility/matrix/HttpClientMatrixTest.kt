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
import java.net.InetSocketAddress
import org.simpleframework.http.Request
import org.simpleframework.http.Response
import org.simpleframework.http.Status
import org.simpleframework.http.core.Container
import org.simpleframework.http.core.ContainerSocketProcessor
import org.simpleframework.transport.connect.SocketConnection
import com.epam.drill.compatibility.context.TestRequestHolder
import com.epam.drill.compatibility.context.DrillRequest
import com.epam.drill.compatibility.context.DRILL_SESSION_ID
import com.epam.drill.compatibility.context.DRILL_TEST_ID

@Suppress("FunctionName")
abstract class HttpClientMatrixTest {

    @Test
    open fun `test request-response with empty thread session and headers data`() = withHttpServer {
        TestRequestHolder.remove()
        val response = callHttpEndpoint(it)
        val responseHeaders = response.first
        val responseBody = response.second
        assertNull(responseHeaders[DRILL_SESSION_ID])
        assertNull(responseHeaders[DRILL_TEST_ID])
        assertEquals("test-request", responseBody)
    }

    @Test
    open fun `test request with existing thread session data`() = withHttpServer {
        TestRequestHolder.store(DrillRequest("session-123", mapOf(DRILL_TEST_ID to "test-data")))
        val response = callHttpEndpoint(it)
        val responseHeaders = response.first
        val responseBody = response.second
        assertEquals("session-123", responseHeaders[DRILL_SESSION_ID])
        assertEquals("test-data", responseHeaders[DRILL_TEST_ID])
        assertEquals("test-request", responseBody)
    }

    protected abstract fun callHttpEndpoint(
        endpoint: String,
        headers: Map<String, String> = emptyMap(),
        body: String = "test-request"
    ): Pair<Map<String, String>, String>

    protected open fun withHttpServer(
        block: (String) -> Unit
    ) = SocketConnection(ContainerSocketProcessor(TestRequestContainer())).use {
        val address = it.connect(InetSocketAddress(0)) as InetSocketAddress
        block("http://localhost:${address.port}")
    }

    protected open class TestRequestContainer : Container {
        override fun handle(request: Request, response: Response) {
            request.header.lines().drop(1)
                .associate { it.substringBefore(":").trim() to it.substringAfter(":", "").trim() }
                .filterKeys { it.startsWith("drill-") }
                .forEach { response.setValue(it.key, it.value) }
            response.status = Status.OK
            response.outputStream.write(request.inputStream.readBytes())
            response.outputStream.close()
        }
    }

}
