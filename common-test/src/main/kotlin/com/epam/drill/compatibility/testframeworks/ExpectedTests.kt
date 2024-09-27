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

class ExpectedTests(private val sessionId: String = System.getProperty("sessionId")) {
    private val expectedTests = mutableListOf<TestData>()

    fun initializeTestData() {
        expectedTests.clear()
        StubAdminClient.clearTestSession(sessionId)
    }

    fun getTestResults(): TestVerificationResults {
        val actualTests = StubAdminClient.pollTests(sessionId, expectedTests.size)
        val isSuccess = actualTests shouldContainsAllTests expectedTests
        return if (!isSuccess) {
            val actualTestData = actualTests.map { it.toTestData() }
            val missingTests = expectedTests
                .filter { it !in actualTestData}
            val extraTests = actualTestData
                .filter { it !in expectedTests }
            TestVerificationResults(false, missingTests, extraTests)
        } else
            TestVerificationResults(true)
    }

    fun add(testClass: Class<*>, name: String, testResult: TestResult, testParams: List<Any?> = emptyList()) {
        expectedTests.add(TestData(testClass.name, name, testResult, testParams.toParams()))
    }
}

class TestVerificationResults(
    val isSuccess: Boolean,
    val missingTests: List<TestData> = emptyList(),
    val extraTests: List<TestData> = emptyList(),
) {
    fun getErrorMessage(): String {
        return "Missing tests: $missingTests\nExtra tests: $extraTests"
    }
}