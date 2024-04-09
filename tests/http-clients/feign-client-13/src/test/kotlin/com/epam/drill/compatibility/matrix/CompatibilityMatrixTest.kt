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
package com.epam.drill.compatibility.matrix

import feign.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

interface MyApiClient {
    @Headers("Content-Type: text/html")
    @RequestLine("POST")
    fun postRequest(@HeaderMap headers: Map<String, String>, body: String): Response
}

class CompatibilityMatrixTest : ClientMatrixTest() {
    override fun callHttpEndpoint(
        endpoint: String,
        headers: Map<String, String>,
        body: String
    ): Pair<Map<String, String>, String> {
        val myApiClient = Feign.builder()
            .target(MyApiClient::class.java, endpoint)

        val response = myApiClient.postRequest(headers, body)
        val responseHeaders = response.headers().mapValues { it.value.first() }
        val responseBody = response.body().asInputStream().convertToString()

        return responseHeaders to responseBody
    }
}

private fun InputStream.convertToString(): String = BufferedReader(InputStreamReader(this)).use { it.readText() }