package com.epam.drill.compatibility.context

@Deprecated("Use DrillTestContext instead")
data class DrillRequest(
    val drillSessionId: String,
    val headers: Map<String, String> = emptyMap()
)