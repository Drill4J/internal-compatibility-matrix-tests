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
package com.epam.drill.compatibility.testframeworks.steps

import com.epam.drill.compatibility.SimpleAdditionService
import com.epam.drill.compatibility.stubs.TestResult
import com.epam.drill.compatibility.testframeworks.ExpectedTests
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.When
import io.cucumber.java.en.Then
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class StepDefinitions {
    private val expectedTests = ExpectedTests()
    private val service = SimpleAdditionService();
    private var number1: Int = 0
    private var number2: Int = 0
    private var result: Int = 0

    @Before
    fun initializeTestData() {
        expectedTests.initializeTestData()
        expectedTests.add("features/example.feature", "Add two numbers", TestResult.PASSED)
    }

    @Given("I have the number {int}")
    fun iHaveTheNumber(num: Int) {
        number1 = num
    }

    @Given("I have another number {int}")
    fun iHaveAnotherNumber(num: Int) {
        number2 = num
    }

    @When("I add the numbers")
    fun iAddTheNumbers() {
        result = service.add(number1, number2)
    }

    @Then("the result should be {int}")
    fun theResultShouldBe(expected: Int) {
        assertEquals(expected, result)
    }

    @After
    fun verifyTestResults() {
        val withLaunchCount = false //TODO There is a bug that the number of launches is 4 times more
        val testResults = expectedTests.getTestResults(withLaunchCount)
        assertTrue(testResults.isSuccess, testResults.getErrorMessage())
    }
}