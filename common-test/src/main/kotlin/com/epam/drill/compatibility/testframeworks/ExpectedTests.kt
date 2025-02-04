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
import com.epam.drill.compatibility.stubs.TestResult

class ExpectedTests(private val sessionId: String = System.getenv("DRILL_SESSION_ID")) {
    private val expectedTests = mutableMapOf<TestData, Int>()

    fun initializeTestData() {
        expectedTests.clear()
    }

    fun getTestResults(withLaunchCount: Boolean = true): TestVerificationResults {
        val actualTests = StubAdminClient.pollTests(sessionId, expectedTests.size)
        val isSuccess = actualTests shouldContainAllTests expectedTests.keys
                && (!withLaunchCount || actualTests.size == expectedTests.values.sum())

         return if (!isSuccess) {
            val actualTestData = actualTests.toTestData()
            val missingTestLaunches = expectedTests
                .filter { it.value > (actualTestData[it.key] ?: 0) }
                .mapValues { actualTestData[it.key] ?: 0 }
            val extraTestLaunches = actualTestData
                .filter { it.value > (expectedTests[it.key] ?: 0) }
            TestVerificationResults(false, missingTestLaunches, extraTestLaunches, expectedTests)
        } else
            TestVerificationResults(true)
    }

    fun add(
        testClass: Class<*>,
        name: String,
        testResult: TestResult,
        testParams: List<Any?> = emptyList(),
        launches: Int = 1,
        additional: TestData.() -> Unit = {}
    ) {
        expectedTests[TestData(testClass.name, name, testResult, testParams.toParams()).apply(additional)] = launches
    }

    fun add(
        testPath: String,
        name: String,
        testResult: TestResult,
        testParams: List<Any?> = emptyList(),
        tags: Set<String> = emptySet(),
        launches: Int = 1
    ) {
        expectedTests[TestData(testPath, name, testResult, testParams.toParams(), tags)] = launches
    }
}

data class TestVerificationResults(
    val isSuccess: Boolean,
    private val missingTestLaunches: Map<TestData, Int> = emptyMap(),
    private val extraTestLaunches: Map<TestData, Int> = emptyMap(),
    private val expectedTest: Map<TestData, Int> = emptyMap(),
) {
    fun getErrorMessage(): String {
        val message = StringBuilder("Test verification failed.\n")
        if (missingTestLaunches.isNotEmpty()) {
            message.append("Missing test launches:\n")
            missingTestLaunches.forEach { (test, launches) ->
                message.append("Test: $test, expected launches: ${expectedTest[test] ?: 0}, actual launches: $launches\n")
            }
        }
        if (extraTestLaunches.isNotEmpty()) {
            message.append("Extra test launches:\n")
            extraTestLaunches.forEach { (test, launches) ->
                message.append("Test: $test, expected launches: ${expectedTest[test] ?: 0}, actual launches: $launches\n")
            }
        }
        return message.toString()
    }
}