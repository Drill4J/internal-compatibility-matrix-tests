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

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class UrlConnectionTest: ClientMatrixTest() {

    override fun callHttpEndpoint(
        endpoint: String,
        headers: Map<String, String>,
        body: String
    ): Pair<Map<String, String>, String> {
        val url = URL(endpoint)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"

        for ((key, value) in headers) {
            connection.setRequestProperty(key, value)
        }

        connection.doOutput = true
        val outputStream = DataOutputStream(connection.outputStream)
        outputStream.writeBytes(body)
        outputStream.flush()
        outputStream.close()

        connection.responseCode
        val responseHeaders = connection.headerFields.map { it.key to it.value.first() }.toMap()
        val responseReader = BufferedReader(InputStreamReader(connection.inputStream))
        val responseBody = StringBuilder()
        var inputLine: String?
        while (responseReader.readLine().also { inputLine = it } != null) {
            responseBody.append(inputLine)
        }
        responseReader.close()
        connection.disconnect()

        return responseHeaders to responseBody.toString()
    }

}