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
package com.epam.drill.compatibility.websocket.clients

import com.epam.drill.compatibility.matrix.SpringMVCWebSocketClientMatrixTest
import javax.websocket.WebSocketContainer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.socket.client.WebSocketClient
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import io.undertow.websockets.jsr.UndertowContainerProvider

@ContextConfiguration(classes = [SpringWs2UndertowWsClientTest.TestWebSocketClientConfig::class])
class SpringWs2UndertowWsClientTest : SpringMVCWebSocketClientMatrixTest() {

    @Configuration
    open class TestWebSocketClientConfig: AbstractTestWebSocketClientConfig() {
        @Bean
        override fun testWebSocketClient(): WebSocketClient =
            StandardWebSocketClient(CustomUndertowContainerProvider().container)
    }

    private class CustomUndertowContainerProvider : UndertowContainerProvider() {
        public override fun getContainer(): WebSocketContainer = super.getContainer()
    }

}
