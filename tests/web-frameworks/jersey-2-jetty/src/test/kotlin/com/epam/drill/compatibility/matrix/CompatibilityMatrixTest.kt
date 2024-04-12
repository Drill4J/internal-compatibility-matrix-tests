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

import com.epam.drill.test.compatibility.SimpleJaxRs2Service
import mu.KotlinLogging
import org.eclipse.jetty.server.Server
import org.glassfish.jersey.jetty.JettyHttpContainerFactory
import org.glassfish.jersey.server.ResourceConfig
import org.junit.AfterClass
import org.junit.BeforeClass


class CompatibilityMatrixTest: CleanServerMatrixTest() {
    override val logger = KotlinLogging.logger {}

    override fun withHttpServer(block: (String) -> Unit) {
        block(server.uri.toString())
    }

    companion object {
        private lateinit var server: Server

        @BeforeClass
        @JvmStatic
        fun setUp() {
            val config = ResourceConfig()
            config.register(SimpleJaxRs2Service::class.java)
            server = JettyHttpContainerFactory.createServer(java.net.URI("http://localhost:8989/"), config)
            server.start()
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            server.stop()
        }
    }
}