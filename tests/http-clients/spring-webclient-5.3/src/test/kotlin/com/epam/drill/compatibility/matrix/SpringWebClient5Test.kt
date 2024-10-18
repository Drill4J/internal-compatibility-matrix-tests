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

import org.springframework.web.reactive.function.client.WebClient

class SpringWebClient5Test : ClientMatrixTest() {
    override fun callHttpEndpoint(
        endpoint: String,
        headers: Map<String, String>,
        body: String
    ): Pair<Map<String, String>, String> {
        val response = WebClient.create()
            .post()
            .uri(endpoint)
            .headers { headers.forEach { (k, v) -> it.add(k, v) } }
            .bodyValue(body)
            .retrieve()
            .toEntity(String::class.java)
            .block()
        val responseHeaders = response?.headers?.flatMap { entry ->
            entry.value.map { value -> entry.key to value }
        }?.associate { it } ?: emptyMap()
        val responseBody = response?.body ?: ""
        return responseHeaders to responseBody
    }
}
