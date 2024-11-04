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
import com.epam.drill.compatibility.stubs.*
import com.sun.net.httpserver.*
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import java.net.*

class StubAdminServer(
    private val host: String,
    private val port: String
) {
    private val logger = KotlinLogging.logger {}
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    init {
        if (host.isEmpty() || port.isEmpty()) {
            throw RuntimeException("Host or Port is not present. Application could not start")
        }
    }

    fun startServer() {
        val storage = StubAdminDataStorage()
        val httpServer = HttpServer.create(InetSocketAddress(host, port.toInt()), 0)

        httpServer.createContext("/data") { httpExchange ->
            httpExchange.get {
                val data = storage.getData()
                sendOk(json.encodeToString(ServerData.serializer(), data))
            }
        }
        httpServer.createContext("/clear") { httpExchange ->
            httpExchange.post {
                val request = httpExchange.requestBody.reader().readText()
                storage.clearSession(json.decodeFromString(SessionIdPayload.serializer(), request).sessionId)
                sendOk()
            }
        }
        httpServer.createContext("/echo") { httpExchange ->
            httpExchange.requestHeaders.forEach {
                httpExchange.responseHeaders[it.key] = it.value
            }
            val requestBody = httpExchange.requestBody.reader().readText()
            httpExchange.sendOk(requestBody)
        }

        httpServer.createContext("/api/data-ingest/tests-metadata") { httpExchange ->
            httpExchange.post {
                val request = httpExchange.requestBody.reader().readText()
                val testsMetadata = json.decodeFromString(AddTestsPayload.serializer(), request)
                logger.info { "Received ${testsMetadata.tests.size} tests metadata from `${testsMetadata.sessionId}`" }
                storage.addTestsMetadata(testsMetadata)
                sendOk()
            }
        }
        httpServer.createContext("/api/data-ingest/sessions") { httpExchange ->
            httpExchange.put {
                val request = httpExchange.requestBody.reader().readText()
                val sessionPayload = json.decodeFromString(SessionPayload.serializer(), request)
                logger.info { "Received session from `${sessionPayload.id}`" }
                storage.addSession(sessionPayload)
                sendOk()
            }
        }
        httpServer.createContext("/api/data-ingest/instances") { httpExchange ->
            httpExchange.put {
                val request = httpExchange.requestBody.reader().readText()
                val instancePayload = json.decodeFromString(InstancePayload.serializer(), request)
                logger.info { "Received instance from `${instancePayload.instanceId}`" }
                storage.addInstance(instancePayload)
                sendOk()
            }
        }
        httpServer.createContext("/api/data-ingest/builds") { httpExchange ->
            httpExchange.put {
                sendOk()
            }
        }
        httpServer.createContext("/api/data-ingest/coverage") { httpExchange ->
            httpExchange.post {
                val request = httpExchange.requestBody.reader().readText()
                val coveragePayload = json.decodeFromString(CoveragePayload.serializer(), request)
                logger.info { "Received ${coveragePayload.coverage.size} coverage from `${coveragePayload.instanceId}`" }
                storage.addCoverage(coveragePayload)
                sendOk()
            }
        }
        httpServer.createContext("/api/data-ingest/methods") { httpExchange ->
            httpExchange.put {
                sendOk()
            }
        }

        httpServer.start()
        println("Stub Admin Server has started ${httpServer.address}")
    }

}


private fun HttpExchange.get(handler: HttpExchange.() -> Unit) {
    handleMethod("GET", handler)
}

private fun HttpExchange.post(handler: HttpExchange.() -> Unit) {
    handleMethod("POST", handler)
}

private fun HttpExchange.put(handler: HttpExchange.() -> Unit) {
    handleMethod("PUT", handler)
}

private fun HttpExchange.handleMethod(method: String, handler: HttpExchange.() -> Unit) {
    if (requestMethod == method) {
        handler()
    } else {
        this.sendResponseHeaders(405, -1)
    }
}

private fun HttpExchange.sendOk(message: String? = null) {
    if (message == null) {
        this.sendResponseHeaders(200, -1)
    } else {
        this.sendResponseHeaders(200, message.toByteArray().size.toLong())
        this.responseBody.use {
            it.write(message.toByteArray())
        }
    }
}
