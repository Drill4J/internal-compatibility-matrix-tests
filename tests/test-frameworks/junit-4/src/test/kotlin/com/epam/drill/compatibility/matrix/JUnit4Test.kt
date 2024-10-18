package com.epam.drill.compatibility.matrix

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
import com.epam.drill.compatibility.testframeworks.ExpectedTests
import com.epam.drill.compatibility.testframeworks.isThereDrillContext
import com.epam.drill.compatibility.stubs.TestResult
import junit.framework.TestCase.assertTrue
import org.junit.*
import org.junit.Ignore
import org.junit.Test

class JUnit4Test {

    companion object {
        private val expectedTests = ExpectedTests()

        @BeforeClass
        @JvmStatic
        fun initializeTestData() {
            expectedTests.initializeTestData()
            expectedTests.add(JUnit4Test::class.java, JUnit4Test::testShouldSkip.name, TestResult.SKIPPED)
        }

        @AfterClass
        @JvmStatic
        fun verifyTestResults() {
            val testResults = expectedTests.getTestResults()
            assertTrue(testResults.getErrorMessage(), testResults.isSuccess)
        }
    }

    @Test
    fun simpleTestMethod() {
        expectedTests.add(this::class.java, ::simpleTestMethod.name, TestResult.PASSED)
        assertTrue(isThereDrillContext())
    }

    @Test
    @Ignore
    fun testShouldSkip() {
        assertTrue(false)
    }


    // TODO Figure out how to test the case when the test fails
//    @Test
//    fun testShouldFail() {
//        expectedTests.add(TestData(::testFailed.name, TestResult.FAILED))
//        assertTrue(isThereDrillContext())
//        fail()
//    }
}
