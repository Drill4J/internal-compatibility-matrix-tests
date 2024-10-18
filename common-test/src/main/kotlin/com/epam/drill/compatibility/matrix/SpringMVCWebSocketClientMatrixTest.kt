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

import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.WebSocketClient
import org.springframework.web.socket.handler.TextWebSocketHandler

abstract class SpringMVCWebSocketClientMatrixTest : SpringCommonWebSocketClientMatrixTest() {

    @Autowired
    lateinit var beanFactory: BeanFactory

    override fun callWebSocketEndpoint(endpoint: String, body: String) = TestWebSocketHandler().run {
        val session = beanFactory.getBean(WebSocketSession::class.java, endpoint, this)
        session.sendMessage(TextMessage(body))
        Thread.sleep(500)
        session.close()
        val handshakeHeaders = this.incomingMessages[0].removePrefix("session-headers:\n").lines()
            .associate { it.substringBefore("=") to it.substringAfter("=", "") }
        this.incomingMessages to handshakeHeaders
    }

    @Configuration
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    abstract class AbstractTestWebSocketClientConfig {
        @Bean
        abstract fun testWebSocketClient(): WebSocketClient
        @Bean
        @Scope(BeanDefinition.SCOPE_PROTOTYPE)
        open fun testWebSocketClientSession(address: String, handler: TextWebSocketHandler): WebSocketSession =
            testWebSocketClient().doHandshake(handler, address).get()
    }

    private class TestWebSocketHandler : TextWebSocketHandler() {
        val incomingMessages = mutableListOf<String>()
        override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
            incomingMessages.add(message.payload)
        }
    }

}
