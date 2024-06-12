package com.epam.drill.compatibility.matrix

import kotlin.test.Test
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@Suppress("FunctionName")
abstract class SpringWebfluxWebSocketClientMatrixTest : AbstractTestServerWebSocketTest() {

    @Test
    fun `test with empty headers request`() = Unit

    @Test
    fun `test with session headers request`() = Unit

}
