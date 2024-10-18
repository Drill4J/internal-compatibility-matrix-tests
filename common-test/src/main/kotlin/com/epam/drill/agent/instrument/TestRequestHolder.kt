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
package com.epam.drill.agent.instrument

import com.epam.drill.compatibility.context.DrillRequest
import com.epam.test.drill.DrillTestContext

@Deprecated("Use DrillTestContext instead")
object TestRequestHolder {
    private val testContext = DrillTestContext()

    fun remove() = testContext.remove()

    fun retrieve(): DrillRequest? {
        val context = testContext.retrieve()
        return if (context != null) {
            DrillRequest(context["drill-session-id"] as String, context)
        } else
            null
    }

    fun store(drillRequest: DrillRequest) {
        val context = HashMap<String, String>()
        context.putAll(drillRequest.headers)
        context.put("drill-session-id", drillRequest.drillSessionId)
        testContext.store(context)
    }

}
