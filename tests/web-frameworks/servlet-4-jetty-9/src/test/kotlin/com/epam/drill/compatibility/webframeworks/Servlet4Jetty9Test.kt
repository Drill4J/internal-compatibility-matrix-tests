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
package com.epam.drill.compatibility.webframeworks

import com.epam.drill.compatibility.matrix.CleanServerMatrixTest
import com.epam.test.drill.compatibility.SimpleHttpServlet4
import mu.KotlinLogging
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.junit.AfterClass
import org.junit.BeforeClass


class CompatibilityMatrixTest: CleanServerMatrixTest() {
    override val logger = KotlinLogging.logger {}

    override fun withHttpServer(block: (String) -> Unit) {
        block("http://localhost:${server.port}/")
    }

    override fun getClassUnderTest(): Class<*> = SimpleHttpServlet4::class.java

    companion object {
        private lateinit var server: Server

        @BeforeClass
        @JvmStatic
        fun setup() {
            server = Server(0)
            val context = ServletContextHandler(ServletContextHandler.SESSIONS)
            context.contextPath = "/"
            server.handler = context
            context.addServlet(ServletHolder(SimpleHttpServlet4()), "/")
            server.start()
        }

        @AfterClass
        @JvmStatic
        fun teardown() {
            server.stop()
        }
    }
}

val Server.port
    get() = (connectors[0] as ServerConnector).localPort