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
import io.undertow.websockets.jsr.UndertowContainerProvider
import jakarta.websocket.ClientEndpointConfig
import jakarta.websocket.WebSocketContainer

class Undertow2WsClientTest : JakartaWebSocketClientMatrixTest() {

    override fun connectToWebsocketAnnotatedEndpoint(endpoint: String) = TestRequestAnnotatedClientEndpoint().run {
        val session = CustomUndertowContainerProvider().container.connectToServer(this, URI(endpoint))
        this to session
    }

    override fun connectToWebsocketInterfaceEndpoint(endpoint: String) = TestRequestInterfaceClientEndpoint().run {
        val session = CustomUndertowContainerProvider().container
            .connectToServer(this, ClientEndpointConfig.Builder.create().build(), URI(endpoint))
        this to session
    }

    private class CustomUndertowContainerProvider : UndertowContainerProvider() {
        public override fun getContainer(): WebSocketContainer = super.getContainer()
    }

}
