package com.epam.drill.compatibility.matrix

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.*
import mu.KotlinLogging
import org.springframework.web.bind.annotation.RestController
import java.net.InetSocketAddress

@RestController
class CompatibilityMatrixTest : AbstractCompatibilityMatrixTest() {

    override val logger = KotlinLogging.logger {}

    override fun withHttpServer(block: (String) -> Unit) {
        val bossGroup = NioEventLoopGroup()
        val workerGroup = NioEventLoopGroup()
        lateinit var serverChannel: Channel
        try {
            serverChannel = ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(TestRequestChannelInitializer())
                .bind(InetSocketAddress(0))
                .sync()
                .channel()
            val address = serverChannel.localAddress() as InetSocketAddress
            block("http://localhost:${address.port}")
        } finally {
            serverChannel.close().sync()
            workerGroup.shutdownGracefully().sync()
            bossGroup.shutdownGracefully().sync()
        }
    }

    private class TestRequestChannelInitializer : ChannelInitializer<SocketChannel>() {
        override fun initChannel(ch: SocketChannel) {
            ch.pipeline().addLast(HttpServerCodec())
            ch.pipeline().addLast(TestRequestChannelHandler())
        }
    }

    private class TestRequestChannelHandler : SimpleChannelInboundHandler<LastHttpContent>() {
        override fun channelRead0(ctx: ChannelHandlerContext, msg: LastHttpContent) {
            val response = DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, msg.retain().content())
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
        }
    }

}
