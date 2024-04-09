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
import org.glassfish.embeddable.*
import org.glassfish.embeddable.archive.ScatteredArchive
import org.junit.AfterClass
import org.junit.BeforeClass
import java.io.File





class CompatibilityMatrixTest: CleanServerMatrixTest() {
    override val logger = KotlinLogging.logger {}

    override fun withHttpServer(block: (String) -> Unit) {
        block("http://localhost:$port/$appName")
    }

    companion object {
        private lateinit var server: GlassFish
        private val port = 9090
        private val appName = "test"

        @BeforeClass
        @JvmStatic
        fun setup() {
            val glassfishProperties = GlassFishProperties()
            glassfishProperties.setPort("http-listener", port)
            server = GlassFishRuntime.bootstrap().newGlassFish(glassfishProperties)
            server.start()

            server.deployer.deploy(ScatteredArchive(appName, ScatteredArchive.Type.WAR).apply {
                addClassPath(File("build/classes/kotlin/main").absoluteFile)
            }.toURI())
        }

        @AfterClass
        @JvmStatic
        fun teardown() {
            server.stop()
            server.dispose()
        }
    }
}