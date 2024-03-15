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
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory
import org.springframework.boot.web.embedded.netty.NettyRouteProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorResourceFactory
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import kotlin.test.Test

@RunWith(SpringRunner::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [SpringWebfluxMatrixTest.SimpleController::class]
)
open class SpringWebfluxMatrixTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun `given Mono class, MonoTransformerObject must propagate drill context`() {
        webTestClient.get().uri("/mono")
            .header("drill-session-id", "session-1")
            .exchange()
            .expectStatus().isOk
            .expectBody<String>()
            .isEqualTo("mono-session-1")
    }

    @RestController
    @EnableAutoConfiguration
    @Configuration
    open class SimpleController {
        @GetMapping("/mono")
        fun getMono(): Mono<String> {
            return Mono.just("mono")
                .subscribeOn(Schedulers.single())
                .map { "$it-${TestRequestHolder.retrieve()?.drillSessionId}" }
        }

    }
}
