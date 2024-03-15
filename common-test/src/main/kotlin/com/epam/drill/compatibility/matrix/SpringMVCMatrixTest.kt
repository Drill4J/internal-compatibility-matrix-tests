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
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Configuration
import org.springframework.http.*
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.test.Test
import kotlin.test.assertEquals


@RunWith(SpringRunner::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [SpringMVCMatrixTest.SimpleController::class]
)
open class SpringMVCMatrixTest {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun `given get controller, server must propagate drill context`() {
        val headers = HttpHeaders()
        headers.set("drill-session-id", "session-1")
        val entity: HttpEntity<String> = HttpEntity(headers)
        val response: ResponseEntity<String> =
            restTemplate.exchange("/", HttpMethod.GET, entity, String::class.java)
        assertEquals(response.statusCode, HttpStatus.OK)
        assertEquals(response.body, "get-controller-session-1")
    }

    @RestController
    @EnableAutoConfiguration
    @Configuration
    open class SimpleController {
        @GetMapping("/")
        fun simpleController(): String {
            return "get-controller-${TestRequestHolder.retrieve()?.drillSessionId}"
        }
    }
}
