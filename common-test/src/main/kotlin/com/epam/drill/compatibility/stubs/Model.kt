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

import kotlinx.serialization.Serializable

typealias MethodProbes = MutableMap<String, BooleanArray>
typealias TestCoverageMap = MutableMap<String, MethodProbes>
typealias TestMap = MutableMap<String, TestLaunchInfo>

@Serializable
data class ServerData(
    val sessions: MutableMap<String, SessionPayload>,
    val tests: MutableMap<String, TestMap>,
    val instances: MutableMap<String, InstancePayload>,
    val coverage: MutableMap<String, TestCoverageMap>,
)

data class TestData(
    val testClass: String,
    val testName: String,
    val testResult: TestResult,
    val testParams: List<String> = emptyList(),
    var tags: Set<String> = emptySet(),
)

@Serializable
class AddTestsPayload(
    val groupId: String,
    val sessionId: String,
    val tests: List<TestLaunchInfo> = emptyList(),
)

@Serializable
data class TestLaunchInfo(
    val testLaunchId: String,
    val testDefinitionId: String,
    val result: TestResult,
    val duration: Int? = null,
    val details: TestDetails,
)

@Serializable
data class TestDetails @JvmOverloads constructor(
    val runner: String = "",
    val path: String = "",
    val testName: String = "",
    val testParams: List<String> = emptyList(),
    val metadata: Map<String, String> = emptyMap(),
    val tags: List<String> = emptyList(),
) : Comparable<TestDetails> {

    override fun compareTo(other: TestDetails): Int {
        return toString().compareTo(other.toString())
    }

    override fun toString(): String {
        return "runner='$runner', path='$path', testName='$testName', params=$testParams"
    }
}

enum class TestResult {
    PASSED,
    FAILED,
    ERROR,
    SKIPPED,
    SMART_SKIPPED,
    UNKNOWN
}

@Serializable
class SessionPayload(
    val id: String,
    val groupId: String,
    val testTaskId: String,
    val startedAt: String
)

@Serializable
data class SessionIdPayload(
    val sessionId: String
)

@Serializable
data class CoveragePayload(
    val groupId: String,
    val appId: String,
    val instanceId: String,
    val commitSha: String?,
    val buildVersion: String?,
    val coverage: List<MethodCoverage>
)

@Serializable
data class MethodCoverage(
    val signature: String,
    val testId: String?,
    val testSessionId: String?,
    val stringProbes: String,
)

@Serializable
data class InstancePayload(
    val groupId: String,
    val appId: String,
    val instanceId: String,
    val commitSha: String? = null,
    val buildVersion: String? = null,
    val envId: String? = null,
)
