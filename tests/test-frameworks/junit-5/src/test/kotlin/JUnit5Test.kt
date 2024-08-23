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
import com.epam.drill.compatibility.stubs.TestResult
import com.epam.drill.compatibility.testframeworks.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class JUnit5Test {

    companion object {
        private val expectedTests = ExpectedTests()

        @BeforeAll
        @JvmStatic
        fun initializeTestData() {
            expectedTests.initializeTestData()
            expectedTests.add(JUnit5Test::class.java, JUnit5Test::testShouldSkip.name, TestResult.SKIPPED)
        }

        @AfterAll
        @JvmStatic
        fun verifyTestResults() {
            val testResults = expectedTests.getTestResults()
            assertTrue(testResults.isSuccess, testResults.getErrorMessage())
        }
    }

    @Test
    fun simpleTestMethodName() {
        expectedTests.add(this::class.java, ::simpleTestMethodName.name, TestResult.PASSED)
        assertTrue(isThereDrillContext())
    }

    @Disabled
    @Test
    fun testShouldSkip() {
    }

    // TODO Figure out how to test the case when the test fails
//    @Test
//    fun testShouldFail() {
//        assertTrue(isThereDrillContext())
//        fail()
//    }
}
