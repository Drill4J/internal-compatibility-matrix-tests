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

import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ClassicHttpResponse
import org.apache.hc.core5.http.io.entity.StringEntity


class CompatibilityMatrixTest: ClientMatrixTest() {

    override fun callHttpEndpoint(
        endpoint: String,
        headers: Map<String, String>,
        body: String
    ): Pair<Map<String, String>, String> = HttpClients.createDefault().run {
        val request = HttpPost(endpoint)
        headers.entries.forEach {
            request.addHeader(it.key, it.value)
        }
        request.entity = StringEntity(body)
        execute(request) { response: ClassicHttpResponse ->
            val responseHeaders = response.headers.associate { it.name to it.value }
            val responseBody = response.entity.content.readBytes().decodeToString()
            responseHeaders to responseBody
        }
    }

}