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
import com.epam.drill.compatibility.stubs.AddTestsPayload
import com.epam.drill.compatibility.stubs.ServerData
import com.epam.drill.compatibility.stubs.SessionPayload
import com.epam.drill.compatibility.stubs.TestInfo
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

class StubAdminDataStorage {
    private val sessions = ConcurrentLinkedQueue<SessionPayload>()
    private val tests = ConcurrentHashMap<String, List<TestInfo>>()

    fun addTestsMetadata(testsMetadata: AddTestsPayload) {
        tests.compute(testsMetadata.sessionId) { _, oldValue ->
            if (oldValue != null)
                oldValue + testsMetadata.tests
            else
                testsMetadata.tests
        }
    }

    fun addSession(sessionPayload: SessionPayload) {
        sessions.add(sessionPayload)
    }

    fun getData() = ServerData(tests, sessions.toList())

    fun clearSession(sessionId: String) {
        tests.remove(sessionId)
    }
}
