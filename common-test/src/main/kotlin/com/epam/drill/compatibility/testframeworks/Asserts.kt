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
package com.epam.drill.compatibility.testframeworks

import com.epam.drill.compatibility.stubs.StubAdminClient
import com.epam.drill.compatibility.stubs.TestData
import com.epam.drill.compatibility.stubs.TestLaunchInfo
import java.lang.reflect.Field

const val DRILL_TEST_ID = "drill-test-id"
const val DRILL_SESSION_ID = "drill-session-id"
const val TEST_CONTEXT_NONE = "TEST_CONTEXT_NONE"

fun TestLaunchInfo.toTestData(): TestData = TestData(
    testClass = this.details.path,
    testName = this.details.testName,
    testResult = this.result,
    testParams = this.details.testParams,
    tags = this.details.tags.toSet()
)

fun Collection<TestLaunchInfo>.toTestData(): Map<TestData, Int> = this
    .groupingBy { it.toTestData() }
    .eachCount()

fun List<Any?>.toParams(): List<String> = this.map { obj ->
    when (obj) {
        null -> "null"
        is Field -> obj.type.simpleName
        else -> obj.javaClass.simpleName.substringBeforeLast("\$")
    }
}

infix fun Collection<TestLaunchInfo>.shouldContainAllTests(expected: Set<TestData>): Boolean {
    return expected == this.toTestData().keys
}

/**
 * Http client sends an empty request to the echo endpoint and checks `drill-test-id` response header.
 * If the header exists, it means that the http client was transformed by Autotest Agent
 * and there was a drill context in the current client thread.
 */
fun isThereDrillContext(): Boolean {
    return isThereDrillContext { StubAdminClient.getEchoHeaders() }
}

fun isThereDrillContext(clientCall: () -> Map<String, String>): Boolean {
    return clientCall()
        .mapKeys { it.key.lowercase() }
        .containsKey("drill-test-id")
}

fun isTestCoveredCode(instanceId: String?, testId: String, classUnderTest: Class<*>): Boolean {
    val className = classUnderTest.name.replace(".", "/")
    return StubAdminClient.pollCoverage(instanceId, testId, className).isNotEmpty()
}

