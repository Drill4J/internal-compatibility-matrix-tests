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
package com.epam.drill.compatibility.stubs

import kotlinx.serialization.json.Json
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients

object StubAdminClient {
    private val host: String = System.getenv("host")
    private val port: Int = System.getenv("port").toInt()
    private val timeout: Int = 10_000
    private val json = Json {
        encodeDefaults = true
    }

    val address = "http://$host:$port"

    fun clearTestSession(sessionId: String) {
        val request = HttpPost("$address/clear")
        request.entity = StringEntity(
            """{ "sessionId": "$sessionId" }""",
            ContentType.APPLICATION_JSON
        )
        HttpClients.createDefault().execute(request)
    }

    fun pollTests(sessionId: String, expectedTests: Int): List<TestInfo> {
        val data = pollData { (it.tests[sessionId]?.size ?: 0) >= expectedTests }
        return data.tests[sessionId]?.values?.toList() ?: emptyList()
    }

    fun pollCoverage(instanceId: String?, testId: String, classId: String): BooleanArray {
        return if (instanceId != null) {
            pollCoverageByInstance(instanceId, testId, classId)
        } else {
            pollCoverageByTest(testId, classId)
        }
    }

    fun pollCoverageByInstance(instanceId: String, testId: String, classId: String): BooleanArray {
        val hasProbesByInstanceAndTestAndClass: (ServerData) -> Boolean = { data ->
            data.coverage[instanceId]?.get(testId)?.get(classId)?.isNotEmpty() ?: false
        }
        val data = pollData { hasProbesByInstanceAndTestAndClass(it) }
        return data.coverage[instanceId]?.get(testId)?.get(classId) ?: BooleanArray(0)
    }

    fun pollCoverageByTest(testId: String, classId: String): BooleanArray {
        val hasProbesByTestAndClass: (TestCoverageMap) -> Boolean = { testMap ->
            testMap[testId]?.get(classId)?.isNotEmpty() ?: false
        }
        val hasProbesByInstanceAndTestAndClass: (ServerData) -> Boolean = { data ->
            data.coverage.values.any { hasProbesByTestAndClass(it) }
        }
        val data = pollData { hasProbesByInstanceAndTestAndClass(it) }
        return data.coverage.values.find { hasProbesByTestAndClass(it) }?.get(testId)?.get(classId) ?: BooleanArray(0)
    }

    private fun pollData(expectedDataHasArrived:  (ServerData) -> Boolean): ServerData {
        var stubData = getStubData()
        val startTime = System.currentTimeMillis()
        val isTimeUp: () -> Boolean = { System.currentTimeMillis() - startTime > timeout }
        while (!isTimeUp() && !expectedDataHasArrived(stubData)) {
            Thread.sleep(100)
            stubData = getStubData()
        }
        println(stubData)
        return stubData
    }

    fun getEchoHeaders(): Map<String, String> {
        return HttpClients.createDefault().execute(
            HttpGet("$address/echo")
        ).allHeaders.associate { it.name to it.value }
    }

    private fun getStubData(): ServerData {
        val response = HttpClients.createDefault().execute(
            HttpGet("$address/data")
        ).entity.content.reader().readText()
        return json.decodeFromString(ServerData.serializer(), response)
    }
}