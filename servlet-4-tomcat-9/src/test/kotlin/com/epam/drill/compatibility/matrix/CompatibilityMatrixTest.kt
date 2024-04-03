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

import mu.KotlinLogging
import org.apache.catalina.WebResourceRoot
import org.apache.catalina.startup.Tomcat
import org.apache.catalina.webresources.DirResourceSet
import org.apache.catalina.webresources.StandardRoot
import org.junit.AfterClass
import org.junit.BeforeClass
import java.io.File


class CompatibilityMatrixTest: CleanServerMatrixTest() {
    override val logger = KotlinLogging.logger {}

    override fun withHttpServer(block: (String) -> Unit) {
        block("http://localhost:${tomcat.connector.localPort}/")
    }

    companion object {
        private lateinit var tomcat: Tomcat

        @BeforeClass
        @JvmStatic
        fun setup() {
            tomcat = Tomcat().apply {
                setPort(0)
                setBaseDir("./build")
                setAddDefaultWebXmlToWebapp(false)
                val ctx = addWebapp("/", File("./build").absolutePath)
                val additionWebInfClasses = File("build/classes")
                val resources: WebResourceRoot = StandardRoot(ctx)
                resources.addPreResources(
                    DirResourceSet(
                        resources, "/WEB-INF/classes",
                        additionWebInfClasses.absolutePath, "/"
                    )
                )
                ctx.resources = resources
                start()
            }
        }

        @AfterClass
        @JvmStatic
        fun teardown() {
            tomcat.stop()
        }
    }
}