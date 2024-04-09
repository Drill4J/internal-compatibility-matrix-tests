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

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class CompatibilityMatrixTest : ClientMatrixTest() {
    override fun callHttpEndpoint(
        endpoint: String,
        headers: Map<String, String>,
        body: String
    ): Pair<Map<String, String>, String> {
        val requestBuilder = Request.Builder().url(endpoint)
        headers.entries.forEach {
            requestBuilder.addHeader(it.key, it.value)
        }
        requestBuilder.post(body.toRequestBody("text/text".toMediaType()))
        OkHttpClient().newCall(requestBuilder.build()).execute().use { response ->
            val responseHeaders = response.headers.toMultimap().mapValues { it.value.joinToString(",") }
            val responseBody = response.body!!.string()
            return responseHeaders to responseBody
        }
    }
}