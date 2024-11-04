package com.epam.drill.compatibility.context

/**
 * This class will be instrumented by Drill4J Agents.
 */
class DrillTestContext {

    fun retrieve(): Map<String, String>? {
        //Will be instrumented
        return null
    }

    fun store(context: Map<String, String>) {
        //Will be instrumented
    }

    fun remove() {
        //Will be instrumented
    }
}