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

import java.net.InetSocketAddress
import java.net.URI
import java.nio.ByteBuffer
import javax.websocket.RemoteEndpoint
import javax.websocket.Session
import org.mockito.Mockito
import io.netty.bootstrap.Bootstrap
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelPromise
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.SimpleUserEventChannelHandler
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.HttpClientCodec
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.codec.http.websocketx.WebSocketVersion
import com.epam.drill.agent.instrument.TestRequestHolder
import com.epam.drill.compatibility.context.DrillRequest

class Netty4WsMessageTest : JavaxWebSocketMessagesMatrixTest() {

    override fun withWebSocketServerAnnotatedEndpoint(block: (String) -> Unit) = withWebSocketServer(
        DefaultProtocolHandlerServerChannelInitializer(TextFrameServerChannelHandler(), BinaryFrameServerChannelHandler()),
        block
    )

    override fun withWebSocketServerInterfaceEndpoint(block: (String) -> Unit) = withWebSocketServer(
        CustomProtocolHandlerServerChannelInitializer(TextFrameServerChannelHandler(), BinaryFrameServerChannelHandler()),
        block
    )

    override fun connectToWebsocketAnnotatedEndpoint(address: String) = TestRequestClientEndpoint().run {
        val session = connectToWebsocketEndpoint(
            address,
            DefaultProtocolHandlerClientChannelInitializer(
                address,
                TextFrameClientChannelHandler(this.incomingMessages, this.incomingContexts),
                BinaryFrameClientChannelHandler(this.incomingMessages, this.incomingContexts)
            )
        )
        this to session
    }

    override fun connectToWebsocketInterfaceEndpoint(address: String) = TestRequestClientEndpoint().run {
        val session = connectToWebsocketEndpoint(
            address,
            CustomProtocolHandlerClientChannelInitializer(
                address,
                TextFrameClientChannelHandler(this.incomingMessages, this.incomingContexts),
                BinaryFrameClientChannelHandler(this.incomingMessages, this.incomingContexts)
            )
        )
        this to session
    }

    private fun withWebSocketServer(initializer: ChannelInitializer<SocketChannel>, block: (String) -> Unit) {
        val bossGroup = NioEventLoopGroup()
        val workerGroup = NioEventLoopGroup()
        lateinit var serverChannel: Channel
        try {
            serverChannel = ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(initializer)
                .bind(InetSocketAddress(0))
                .sync()
                .channel()
            val address = serverChannel.localAddress() as InetSocketAddress
            block("ws://localhost:${address.port}")
        } finally {
            serverChannel.close().sync()
            workerGroup.shutdownGracefully().sync()
            bossGroup.shutdownGracefully().sync()
        }
    }

    private fun connectToWebsocketEndpoint(
        endpoint: String,
        initializer: TestClientChannelInitializer
    ): Session {
        val uri = URI(endpoint)
        val group = NioEventLoopGroup()
        val channel = Bootstrap()
            .group(group)
            .channel(NioSocketChannel::class.java)
            .handler(initializer)
            .connect(uri.host, uri.port)
            .sync()
            .channel()
        val session = Mockito.mock(Session::class.java)
        val basicRemote = Mockito.mock(RemoteEndpoint.Basic::class.java)
        val asyncRemote = Mockito.mock(RemoteEndpoint.Async::class.java)
        Mockito.`when`(session.basicRemote).thenReturn(basicRemote)
        Mockito.`when`(session.asyncRemote).thenReturn(asyncRemote)
        Mockito.`when`(session.close()).then {
            group.shutdownGracefully().sync()
        }
        Mockito.`when`(basicRemote.sendText(Mockito.anyString())).then {
            channel.writeAndFlush(TextWebSocketFrame(it.arguments[0] as String)).sync()
        }
        Mockito.`when`(basicRemote.sendBinary(Mockito.any(ByteBuffer::class.java))).then {
            channel.writeAndFlush(BinaryWebSocketFrame(Unpooled.wrappedBuffer(it.arguments[0] as ByteBuffer))).sync()
        }
        Mockito.`when`(asyncRemote.sendText(Mockito.anyString())).then {
            channel.writeAndFlush(TextWebSocketFrame(it.arguments[0] as String))
        }
        Mockito.`when`(asyncRemote.sendBinary(Mockito.any(ByteBuffer::class.java))).then {
            channel.writeAndFlush(BinaryWebSocketFrame(Unpooled.wrappedBuffer(it.arguments[0] as ByteBuffer)))
        }
        initializer.handshakePromise.sync()
        return session
    }

    class TestRequestClientEndpoint : TestRequestEndpoint {
        override val incomingMessages = mutableListOf<String>()
        override val incomingContexts = mutableListOf<DrillRequest?>()
    }

    private class DefaultProtocolHandlerServerChannelInitializer(
        private val textMessageHandler: SimpleChannelInboundHandler<TextWebSocketFrame>,
        private val binaryMessageHandler: SimpleChannelInboundHandler<BinaryWebSocketFrame>
    ) : ChannelInitializer<SocketChannel>() {
        override fun initChannel(ch: SocketChannel) {
            ch.pipeline().addLast(HttpServerCodec())
            ch.pipeline().addLast(WebSocketServerProtocolHandler("/"))
            ch.pipeline().addLast(textMessageHandler)
            ch.pipeline().addLast(binaryMessageHandler)
        }
    }

    private class CustomProtocolHandlerServerChannelInitializer(
        private val textMessageHandler: SimpleChannelInboundHandler<TextWebSocketFrame>,
        private val binaryMessageHandler: SimpleChannelInboundHandler<BinaryWebSocketFrame>
    ) : ChannelInitializer<SocketChannel>() {
        override fun initChannel(ch: SocketChannel) {
            ch.pipeline().addLast(HttpServerCodec())
            ch.pipeline().addLast(HttpWebSocketHandshakeServerHandler(textMessageHandler, binaryMessageHandler))
        }
    }

    private class HttpWebSocketHandshakeServerHandler(
        private val textMessageHandler: SimpleChannelInboundHandler<TextWebSocketFrame>,
        private val binaryMessageHandler: SimpleChannelInboundHandler<BinaryWebSocketFrame>
    ) : SimpleChannelInboundHandler<HttpRequest>() {
        override fun channelRead0(ctx: ChannelHandlerContext, msg: HttpRequest) {
            if (HttpHeaderValues.UPGRADE.contentEqualsIgnoreCase(msg.headers().get(HttpHeaderNames.CONNECTION)) &&
                HttpHeaderValues.WEBSOCKET.contentEqualsIgnoreCase(msg.headers().get(HttpHeaderNames.UPGRADE))) {
                ctx.pipeline().remove(this)
                ctx.pipeline().addLast(textMessageHandler)
                ctx.pipeline().addLast(binaryMessageHandler)
                val wsUrl = "ws://${msg.headers().get(HttpHeaderNames.HOST)}${msg.uri()}"
                WebSocketServerHandshakerFactory(wsUrl, null, true).newHandshaker(msg)
                    ?.handshake(ctx.channel(), msg)
                    ?: WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel())
            }
        }
    }

    private class TextFrameServerChannelHandler : SimpleChannelInboundHandler<TextWebSocketFrame>(), TestRequestEndpoint {
        override val incomingMessages = TestRequestEndpoint.incomingMessages
        override val incomingContexts = TestRequestEndpoint.incomingContexts
        override fun channelRead0(ctx: ChannelHandlerContext, msg: TextWebSocketFrame) {
            val text = msg.text()
            incomingMessages.add(text)
            incomingContexts.add(TestRequestHolder.retrieve())
            TestRequestHolder.store(DrillRequest("$text-response-session", mapOf("drill-data" to "$text-response-data")))
            ctx.writeAndFlush(TextWebSocketFrame("$text-response"))
            TestRequestHolder.remove()
        }
    }

    private class BinaryFrameServerChannelHandler : SimpleChannelInboundHandler<BinaryWebSocketFrame>(), TestRequestEndpoint {
        override val incomingMessages = TestRequestEndpoint.incomingMessages
        override val incomingContexts = TestRequestEndpoint.incomingContexts
        override fun channelRead0(ctx: ChannelHandlerContext, msg: BinaryWebSocketFrame) {
            val message = msg.content()
            val text = ByteArray(message.readableBytes()).also(message::readBytes).decodeToString()
            incomingMessages.add(text)
            incomingContexts.add(TestRequestHolder.retrieve())
            TestRequestHolder.store(DrillRequest("$text-response-session", mapOf("drill-data" to "$text-response-data")))
            ctx.writeAndFlush(BinaryWebSocketFrame(Unpooled.wrappedBuffer("$text-response".encodeToByteArray())))
            TestRequestHolder.remove()
        }
    }

    private abstract class TestClientChannelInitializer(
        endpoint: String
    ) : ChannelInitializer<SocketChannel>() {
        lateinit var handshakePromise: ChannelPromise
        protected val handshaker: WebSocketClientHandshaker = WebSocketClientHandshakerFactory.newHandshaker(
            URI(endpoint), WebSocketVersion.V13, null, true, DefaultHttpHeaders()
        )
    }

    private class DefaultProtocolHandlerClientChannelInitializer(
        endpoint: String,
        private val textMessageHandler: SimpleChannelInboundHandler<TextWebSocketFrame>,
        private val binaryMessageHandler: SimpleChannelInboundHandler<BinaryWebSocketFrame>
    ) : TestClientChannelInitializer(endpoint) {
        override fun initChannel(ch: SocketChannel) {
            handshakePromise = ch.pipeline().newPromise()
            ch.pipeline().addLast(HttpClientCodec())
            ch.pipeline().addLast(HttpObjectAggregator(2048))
            ch.pipeline().addLast(WebSocketClientProtocolHandler(handshaker))
            ch.pipeline().addLast(HandshakeCompleteClientEventHandler(handshakePromise))
            ch.pipeline().addLast(textMessageHandler)
            ch.pipeline().addLast(binaryMessageHandler)
        }
    }

    private class CustomProtocolHandlerClientChannelInitializer(
        endpoint: String,
        private val textMessageHandler: SimpleChannelInboundHandler<TextWebSocketFrame>,
        private val binaryMessageHandler: SimpleChannelInboundHandler<BinaryWebSocketFrame>
    ) : TestClientChannelInitializer(endpoint) {
        override fun initChannel(ch: SocketChannel) {
            handshakePromise = ch.pipeline().newPromise()
            ch.pipeline().addLast(HttpClientCodec())
            ch.pipeline().addLast(HttpObjectAggregator(2048))
            ch.pipeline().addLast(
                HttpWebSocketHandshakeClientHandler(handshaker, handshakePromise, textMessageHandler, binaryMessageHandler)
            )
        }
    }

    private class HttpWebSocketHandshakeClientHandler(
        private val handshaker: WebSocketClientHandshaker,
        private val handshakePromise: ChannelPromise,
        private val textMessageHandler: SimpleChannelInboundHandler<TextWebSocketFrame>,
        private val binaryMessageHandler: SimpleChannelInboundHandler<BinaryWebSocketFrame>
    ) : SimpleChannelInboundHandler<FullHttpResponse>() {
        override fun channelActive(ctx: ChannelHandlerContext) {
            super.channelActive(ctx)
            handshaker.handshake(ctx.channel())
        }
        override fun channelRead0(ctx: ChannelHandlerContext, msg: FullHttpResponse) {
            if (!handshaker.isHandshakeComplete) {
                handshaker.finishHandshake(ctx.channel(), msg)
                ctx.pipeline().remove(this)
                ctx.pipeline().addLast(textMessageHandler)
                ctx.pipeline().addLast(binaryMessageHandler)
                handshakePromise.setSuccess()
            }
        }
    }

    private class HandshakeCompleteClientEventHandler(
        private val handshakePromise: ChannelPromise
    ) : SimpleUserEventChannelHandler<WebSocketClientProtocolHandler.ClientHandshakeStateEvent>() {
        override fun eventReceived(ctx: ChannelHandlerContext, evt: WebSocketClientProtocolHandler.ClientHandshakeStateEvent) {
            if (evt == WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE)
                handshakePromise.setSuccess()
            if (evt == WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_TIMEOUT)
                handshakePromise.setFailure(WebSocketHandshakeException(evt.name))
        }
    }

    private class TextFrameClientChannelHandler(
        override val incomingMessages: MutableList<String>,
        override val incomingContexts: MutableList<DrillRequest?>
    ) : SimpleChannelInboundHandler<TextWebSocketFrame>(), TestRequestEndpoint {
        override fun channelRead0(ctx: ChannelHandlerContext, msg: TextWebSocketFrame) {
            incomingMessages.add(msg.text())
            incomingContexts.add(TestRequestHolder.retrieve())
        }
    }

    private class BinaryFrameClientChannelHandler(
        override val incomingMessages: MutableList<String>,
        override val incomingContexts: MutableList<DrillRequest?>
    ) : SimpleChannelInboundHandler<BinaryWebSocketFrame>(), TestRequestEndpoint {
        override fun channelRead0(ctx: ChannelHandlerContext, msg: BinaryWebSocketFrame) {
            val text = msg.content().let { ByteArray(it.readableBytes()).also(it::readBytes).decodeToString() }
            incomingMessages.add(text)
            incomingContexts.add(TestRequestHolder.retrieve())
        }
    }

}
