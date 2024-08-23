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
import com.epam.drill.compatibility.stubs.TestResult
import com.epam.drill.compatibility.testframeworks.isThereDrillContext
import org.testng.Assert.*
import org.testng.annotations.*

class TestNG7Test {

    private val expectedTests = ExpectedTests("testng-7.4")

    @BeforeClass
    fun initializeTestData() {
        expectedTests.initializeTestData()
    // expectedTests.add(this::class.java, ::testShouldSkip.name, TestResult.SKIPPED) TODO EPMDJ-10906
    }

    @AfterClass
    fun verifyTestResults() {
        val testResults = expectedTests.getTestResults()
        assertTrue(testResults.isSuccess, testResults.getErrorMessage())
    }

    @DataProvider
    fun dataProvider() = arrayOf<Array<Any?>>(
        arrayOf(1, "first"),
        arrayOf(2, "second"),
        arrayOf(null, null)
    )

    @Test
    fun simpleTestMethodName() {
        expectedTests.add(this::class.java, ::simpleTestMethodName.name, TestResult.PASSED)
        assertTrue(isThereDrillContext())
    }

    @Test(dataProvider = "dataProvider")
    fun parametrizedTest(int: Int?, string: String?) {
        expectedTests.add(this::class.java, ::parametrizedTest.name, TestResult.PASSED, listOf(int, string))
        assertTrue(isThereDrillContext())
    }

    @Test(enabled = false)
    fun testShouldSkip() {
    }

    //    TODO Figure out how to test the case when the test fails
//    @Test
//    fun testShouldFail() {
//        expectedTests.add(::testFailed.toTestData(TestResult.FAILED))
//        assertTrue(isThereDrillContext())
//        fail()
//    }

}
