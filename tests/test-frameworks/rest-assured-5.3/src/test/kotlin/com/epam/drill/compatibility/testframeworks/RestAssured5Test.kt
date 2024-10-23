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
import com.epam.drill.compatibility.testframeworks.isThereDrillContext
import io.restassured.RestAssured
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RestAssured5Test {
    @Test
    fun `given RestAssured 5_3, TestAgent should add Drill headers to requests`() {
        assertTrue(isThereDrillContext {
            val request: RequestSpecification = RestAssured.given()
            val response: Response = request.post(StubAdminClient.address + "/echo")
            val responseHeaders = response.headers.asList().associate { it.name to it.value }
            responseHeaders
        })
    }
}