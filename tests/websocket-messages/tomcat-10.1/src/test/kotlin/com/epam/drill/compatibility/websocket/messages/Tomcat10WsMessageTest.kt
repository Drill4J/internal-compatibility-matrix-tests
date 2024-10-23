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

import java.net.URI
import java.util.logging.LogManager
import jakarta.servlet.ServletContextEvent
import jakarta.websocket.ClientEndpointConfig
import jakarta.websocket.server.ServerContainer
import jakarta.websocket.server.ServerEndpointConfig
import org.apache.catalina.servlets.DefaultServlet
import org.apache.catalina.startup.Tomcat
import org.apache.tomcat.websocket.WsWebSocketContainer
import org.apache.tomcat.websocket.server.Constants
import org.apache.tomcat.websocket.server.WsContextListener

class Tomcat10WsMessageTest : JakartaWebSocketMessagesMatrixTest() {

    override fun withWebSocketServerAnnotatedEndpoint(block: (String) -> Unit) =
        withWebSocketEndpoint(AnnotatedEndpointApplicationListener::class.java.name, block)

    override fun withWebSocketServerInterfaceEndpoint(block: (String) -> Unit) =
        withWebSocketEndpoint(InterfaceEndpointApplicationListener::class.java.name, block)

    override fun connectToWebsocketAnnotatedEndpoint(address: String) = TestRequestClientAnnotatedEndpoint().run {
        val session = WsWebSocketContainer().connectToServer(this, URI(address))
        this to session
    }

    override fun connectToWebsocketInterfaceEndpoint(address: String) = TestRequestClientInterfaceEndpoint().run {
        val session = WsWebSocketContainer()
            .connectToServer(this, ClientEndpointConfig.Builder.create().build(), URI(address))
        this to session
    }

    private fun withWebSocketEndpoint(applicationListener: String, block: (String) -> Unit)  = Tomcat().run {
        try {
            LogManager.getLogManager().readConfiguration(ClassLoader.getSystemResourceAsStream("logging.properties"))
            this.setBaseDir("./build")
            this.setPort(0)
            val context = this.addContext("", null)
            this.addServlet(context.path, DefaultServlet::class.simpleName, DefaultServlet())
            context.addServletMappingDecoded("/", DefaultServlet::class.simpleName)
            context.addApplicationListener(applicationListener)
            this.start()
            block("ws://localhost:${connector.localPort}")
        } finally {
            this.stop()
        }
    }

    class AnnotatedEndpointApplicationListener : WsContextListener() {
        override fun contextInitialized(sce: ServletContextEvent) {
            super.contextInitialized(sce)
            val container = sce.servletContext.getAttribute(Constants.SERVER_CONTAINER_SERVLET_CONTEXT_ATTRIBUTE)
            (container as ServerContainer).addEndpoint(TestRequestServerAnnotatedEndpoint::class.java)
        }
    }

    class InterfaceEndpointApplicationListener : WsContextListener() {
        override fun contextInitialized(sce: ServletContextEvent) {
            super.contextInitialized(sce)
            val container = sce.servletContext.getAttribute(Constants.SERVER_CONTAINER_SERVLET_CONTEXT_ATTRIBUTE)
            val config = ServerEndpointConfig.Builder.create(TestRequestServerInterfaceEndpoint::class.java, "/").build()
            (container as ServerContainer).addEndpoint(config)
        }
    }

}
