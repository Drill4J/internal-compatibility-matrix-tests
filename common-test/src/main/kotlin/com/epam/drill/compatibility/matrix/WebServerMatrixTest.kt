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
import com.epam.drill.compatibility.context.DRILL_SESSION_ID
import com.epam.drill.compatibility.context.DRILL_TEST_ID
import com.epam.drill.compatibility.context.TEST_CONTEXT_NONE
import com.epam.drill.compatibility.testframeworks.isTestCoveredCode
import mu.KLogger
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.test.*

@Suppress("FunctionName")
abstract class WebServerMatrixTest {
    protected abstract val logger: KLogger
    private val agentInstanceId: String? = System.getenv("DRILL_INSTANCE_ID")

    @Test
    fun `test with empty headers request`() = withHttpServer { it ->
        TestRequestHolder.remove() // necessary while HttpURLConnection is being instrumented by Drill4J
        val response = callHttpEndpoint(it)
        assertNull(response.headers[DRILL_SESSION_ID])
        assertNull(response.headers[DRILL_TEST_ID])
        assertEquals("test-request", response.body)
        assertTrue(isTestCoveredCode(agentInstanceId, TEST_CONTEXT_NONE, getSignatureUnderTest()))
    }

    @Test
    fun `test with session headers request`() = withHttpServer { endpoint ->
        val testId = "test-data"
        val requestHeaders = mapOf(
            DRILL_SESSION_ID to "session-123",
            DRILL_TEST_ID to testId
        )
        val response = callHttpEndpoint(endpoint, requestHeaders)
        assertEquals("session-123", response.headers[DRILL_SESSION_ID])
        assertEquals(testId, response.headers[DRILL_TEST_ID])
        assertEquals("test-request", response.body)
        assertTrue(isTestCoveredCode(agentInstanceId, testId, getSignatureUnderTest()))
    }

    protected abstract fun withHttpServer(block: (String) -> Unit)

    abstract fun getClassUnderTest(): Class<*>
    open fun getMethodUnderTest(): String = ""
    @Suppress("NO_REFLECTION_IN_CLASS_PATH")
    @OptIn(ExperimentalStdlibApi::class)
    fun getSignatureUnderTest(): String {
        val className = getClassUnderTest().name.replace(".", "/")
        val methodName = getMethodUnderTest()
        return "$className:$methodName"
    }

    private fun callHttpEndpoint(
        endpoint: String,
        headers: Map<String, String> = emptyMap(),
        contentType: String = "text/plain",
        body: String = "test-request"
    ): HttpResponse {
        val url = URL(endpoint)
        val connection = url.openConnection() as HttpURLConnection

        try {
            logger.trace { "callHttpEndpoint: Requesting $endpoint: headers=$headers, body=$body" }
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "$contentType; charset=UTF-8")
            headers.entries.forEach {
                connection.setRequestProperty(it.key, it.value)
            }
            connection.outputStream.use { os ->
                OutputStreamWriter(os, Charsets.UTF_8).use { writer ->
                    writer.write(body)
                }
            }
            return HttpResponse(
                status = connection.responseCode,
                headers = connection.headerFields.mapValues { it.value.joinToString(",") },
                body = connection.inputStream.bufferedReader().readText()
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