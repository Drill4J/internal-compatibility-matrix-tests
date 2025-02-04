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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

class StubAdminDataStorage {
    private val data = ServerData(ConcurrentHashMap(), ConcurrentHashMap(), ConcurrentHashMap(), ConcurrentHashMap())

    fun addTestsMetadata(testsMetadata: AddTestsPayload) {
        val sessionTests = data.tests.computeIfAbsent(testsMetadata.sessionId) { ConcurrentHashMap() }
        testsMetadata.tests.forEach { testInfo ->
            sessionTests[testInfo.testLaunchId] = testInfo
        }
    }

    fun addSession(sessionPayload: SessionPayload) {
        data.sessions[sessionPayload.id] = sessionPayload
    }

    fun addInstance(instancePayload: InstancePayload) {
        data.instances[instancePayload.instanceId] = instancePayload
    }

    fun addCoverage(coveragePayload: CoveragePayload) {
        val instanceData = data.coverage.computeIfAbsent(coveragePayload.instanceId) { ConcurrentHashMap() }
        coveragePayload.coverage.forEach { coverage ->
            val testData = instanceData.computeIfAbsent(coverage.testId) { ConcurrentHashMap() }
            val classProbes = testData.computeIfAbsent(coverage.classname) { BooleanArray(coverage.probes.size) }
            classProbes.forEachIndexed { index, probe ->
                classProbes[index] = probe || coverage.probes[index]
            }
        }
    }

    fun getData() = data

    fun clearSession(sessionId: String) {
        data.tests.remove(sessionId)
    }
}
