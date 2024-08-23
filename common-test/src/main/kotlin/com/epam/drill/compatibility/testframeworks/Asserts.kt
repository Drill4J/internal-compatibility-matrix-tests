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
import com.epam.drill.compatibility.stubs.TestInfo
import java.lang.reflect.Field

fun TestInfo.toTestData(): TestData = TestData(
    testClass = this.details.path,
    testName = this.details.testName,
    testResult = this.result,
    testParams = this.details.params["methodParams"]?.toParams() ?: emptyList()
)

fun List<Any?>.toParams(): List<String> = this.map { obj ->
    when (obj) {
        null -> "null"
        is Field -> obj.type.simpleName
        else -> obj.javaClass.simpleName.substringBeforeLast("\$")
    }
}

fun String.toParams(): List<String> {
    val withoutSquareBrackets = this.replace(Regex("\\[.*?\\]"), "")
    val withoutParentheses = withoutSquareBrackets.trim('(', ')')
    val withoutEndCommas = withoutParentheses.trimEnd(',')

    return if (withoutEndCommas.isEmpty()) {
        emptyList()
    } else {
        withoutEndCommas.split(",").map { it.trim() }
    }
}

infix fun List<TestInfo>.shouldContainsAllTests(expected: Collection<TestData>): Boolean {
    return expected.size == this.size
            && expected.containsAll(this.map { it.toTestData() })
}

/**
 * Http client sends an empty request to the echo endpoint and checks `drill-test-id` response header.
 * If the header exists, it means that the http client was transformed by Autotest Agent
 * and there was a drill context in the current client thread.
 */
fun isThereDrillContext(): Boolean {
    return StubAdminClient.getEchoHeaders()
        .mapKeys { it.key.lowercase() }
        .containsKey("drill-test-id")
}

