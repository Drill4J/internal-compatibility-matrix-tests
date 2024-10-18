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

import com.epam.test.drill.compatibility.SimpleJaxRs2Service
import mu.KotlinLogging
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean
import org.junit.AfterClass
import org.junit.BeforeClass
import java.net.ServerSocket


class Cxf3JettyTest: CleanServerMatrixTest() {
    override val logger = KotlinLogging.logger {}

    override fun withHttpServer(block: (String) -> Unit) {
        block(server.address)
    }

    companion object {
        private val port = ServerSocket(0).use { it.localPort }
        private lateinit var server: JAXRSServerFactoryBean

        @BeforeClass
        @JvmStatic
        fun setUp() {
            server = JAXRSServerFactoryBean()
            server.address = "http://localhost:$port/"
            server.setResourceClasses(SimpleJaxRs2Service::class.java)
            server.create()
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            server.server.stop()
        }
    }
}