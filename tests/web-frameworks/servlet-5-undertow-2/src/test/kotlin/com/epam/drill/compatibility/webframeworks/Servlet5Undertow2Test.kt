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
import com.epam.test.drill.compatibility.SimpleHttpServlet5
import io.undertow.Handlers
import io.undertow.Undertow
import io.undertow.servlet.Servlets
import io.undertow.servlet.Servlets.servlet
import mu.KotlinLogging
import org.junit.AfterClass
import org.junit.BeforeClass


class Servlet5Undertow2Test : CleanServerMatrixTest() {
    override val logger = KotlinLogging.logger {}

    override fun withHttpServer(block: (String) -> Unit) {
        block("http://localhost:$port/")
    }

    companion object {
        private lateinit var server: Undertow
        private val port = 8888

        @BeforeClass
        @JvmStatic
        fun startServer() {
            val servletBuilder = Servlets.deployment().setClassLoader(
                Servlet5Undertow2Test::class.java.classLoader
            )
                .setDeploymentName("testApp.war")
                .setContextPath("/")
                .addServlets(
                    servlet("SimpleServlet", SimpleHttpServlet5::class.java)
                        .addMapping("/")
                )
            val manager = Servlets.defaultContainer().addDeployment(servletBuilder)
            manager.deploy()
            val path = Handlers.path(Handlers.redirect("/"))
                .addPrefixPath("/", manager.start())
            server = Undertow.builder()
                .addHttpListener(port, "localhost")
                .setHandler(path)
                .build()
            server.start()
        }

        @AfterClass
        @JvmStatic
        fun stopServer() {
            server.stop()
        }
    }
}