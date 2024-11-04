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

import com.epam.drill.compatibility.context.TestRequestHolder
import com.epam.drill.compatibility.testframeworks.DRILL_SESSION_ID
import com.epam.drill.compatibility.testframeworks.DRILL_TEST_ID
import com.epam.drill.compatibility.testframeworks.TEST_CONTEXT_NONE
import com.epam.drill.compatibility.testframeworks.isTestCoveredCode
import mu.KLogger
import java.net.HttpURLConnection
import java.net.URL
import kotlin.test.*

@Suppress("FunctionName")
abstract class CleanServerMatrixTest {
    protected abstract val logger: KLogger
    private val agentInstanceId: String? = System.getenv("DRILL_INSTANCE_ID")

    @Test
    fun `test with empty headers request`() = withHttpServer {
        TestRequestHolder.remove() // necessary while HttpURLConnection is being instrumented by Drill4J
        val response = callHttpEndpoint(it)
        assertNull(response.headers[DRILL_SESSION_ID])
        assertNull(response.headers[DRILL_TEST_ID])
        assertEquals("test-request", response.body)
        getClassUnderTest()?.let { assertTrue(isTestCoveredCode(agentInstanceId, TEST_CONTEXT_NONE, it))  }
    }

    @Test
    fun `test with session headers request`() {
        val testId = "test-data"
        withHttpServer { endpoint ->
            val requestHeaders = mapOf(
                DRILL_SESSION_ID to "session-123",
                DRILL_TEST_ID to testId
            )
            val response = callHttpEndpoint(endpoint, requestHeaders)
            assertEquals("session-123", response.headers[DRILL_SESSION_ID])
            assertEquals(testId, response.headers[DRILL_TEST_ID])
            assertEquals("test-request", response.body)
            getClassUnderTest()?.let { assertTrue(isTestCoveredCode(agentInstanceId, testId, it))  }
        }
    }

    protected abstract fun withHttpServer(block: (String) -> Unit)

    protected open fun getClassUnderTest(): Class<*>? = null

    private fun callHttpEndpoint(
        endpoint: String,
        headers: Map<String, String> = emptyMap(),
        contentType: String = "text/plain",
        body: String = "test-request"
    ): HttpResponse {
        lateinit var connection: HttpURLConnection
        try {
            logger.trace { "callHttpEndpoint: Requesting $endpoint: headers=$headers, body=$body" }
            connection = URL(endpoint).openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", contentType)
            headers.entries.forEach {
                connection.setRequestProperty(it.key, it.value)
            }
            connection.doOutput = true
            connection.outputStream.write(body.encodeToByteArray())
            connection.outputStream.close()
            val responseCode = connection.responseCode
            val responseHeaders = connection.headerFields.mapValues { it.value.joinToString(",") }
            val responseBody = connection.inputStream.readBytes().decodeToString()
            connection.inputStream.close()
            logger.trace { "callHttpEndpoint: Response from $endpoint: headers=$responseHeaders, body=$responseBody" }
            return HttpResponse(
                status = responseCode,
                headers = responseHeaders,
                body = responseBody
            )
        } finally {
            connection.disconnect()
        }
    }
}

data class HttpResponse(
    val status: Int,
    val headers: Map<String, String>,
    val body: String
)