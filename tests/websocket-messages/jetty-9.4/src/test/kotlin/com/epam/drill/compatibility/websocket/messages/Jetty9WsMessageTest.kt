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
import javax.servlet.ServletContext
import javax.websocket.ClientEndpointConfig
import javax.websocket.server.ServerEndpointConfig
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.websocket.jsr356.ClientContainer
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer

class Jetty9WsMessageTest : JavaxWebSocketMessagesMatrixTest() {

    override fun withWebSocketServerAnnotatedEndpoint(block: (String) -> Unit) =
        withWebSocketEndpoint(AnnotatedEndpointConfigurator(), block)

    override fun withWebSocketServerInterfaceEndpoint(block: (String) -> Unit) =
        withWebSocketEndpoint(InterfaceEndpointConfigurator(), block)

    override fun connectToWebsocketAnnotatedEndpoint(address: String) = TestRequestClientAnnotatedEndpoint().run {
        val session = ClientContainer().also(ClientContainer::start).connectToServer(this, URI(address))
        this to session
    }

    override fun connectToWebsocketInterfaceEndpoint(address: String) = TestRequestClientInterfaceEndpoint().run {
        val session = ClientContainer().also(ClientContainer::start)
            .connectToServer(this, ClientEndpointConfig.Builder.create().build(), URI(address))
        this to session
    }

    private fun withWebSocketEndpoint(
        configurator: WebSocketServerContainerInitializer.Configurator,
        block: (String) -> Unit
    ) = Server().run {
        try {
            val connector = ServerConnector(this)
            this.connectors = arrayOf(connector)
            val context = ServletContextHandler(null, "/", ServletContextHandler.SESSIONS)
            this.handler = context
            WebSocketServerContainerInitializer.configure(context, configurator)
            this.start()
            block("ws://localhost:${connector.localPort}")
        } finally {
            this.stop()
        }
    }

    private class AnnotatedEndpointConfigurator : WebSocketServerContainerInitializer.Configurator {
        override fun accept(servletContext: ServletContext, serverContainer: ServerContainer) {
            serverContainer.addEndpoint(TestRequestServerAnnotatedEndpoint::class.java)
        }
    }

    private class InterfaceEndpointConfigurator : WebSocketServerContainerInitializer.Configurator {
        override fun accept(servletContext: ServletContext, serverContainer: ServerContainer) {
            val config = ServerEndpointConfig.Builder.create(TestRequestServerInterfaceEndpoint::class.java, "/").build()
            serverContainer.addEndpoint(config)
        }
    }

}
