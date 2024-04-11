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

import com.epam.drill.agent.instrument.TestRequestHolder
import com.epam.drill.common.agent.request.DrillRequest
import java.util.concurrent.Future
import kotlin.test.Test
import kotlin.test.assertEquals

abstract class AsyncMatrixTest {
    @Test
    open fun `test async communication without existing thread session data`() {
        TestRequestHolder.remove()

        val future = callAsyncCommunication {
            val drillRequest = TestRequestHolder.retrieve()
            "test-${drillRequest?.drillSessionId}"
        }
        val response = future.get()

        assertEquals("test-null", response)
    }

    @Test
    open fun `test async communication with existing thread session data`() {
        TestRequestHolder.store(DrillRequest("session-123"))

        val future = callAsyncCommunication {
            val drillRequest = TestRequestHolder.retrieve()
            "test-${drillRequest?.drillSessionId}"
        }
        val response = future.get()

        assertEquals("test-session-123", response)
    }

    abstract fun callAsyncCommunication(task: () -> String): Future<String>
}