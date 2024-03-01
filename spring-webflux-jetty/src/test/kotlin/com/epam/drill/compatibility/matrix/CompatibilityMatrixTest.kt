package com.epam.drill.compatibility.matrix

import mu.KotlinLogging
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.handler.AbstractHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class CompatibilityMatrixTest : AbstractCompatibilityMatrixTest() {
    override val logger = KotlinLogging.logger {}

    override fun withHttpServer(block: (String) -> Unit) = Server().run {
        try {
            val connector = ServerConnector(this)
            this.connectors = arrayOf(connector)
            this.handler = TestRequestHandler
            this.start()
            block("http://localhost:${connector.localPort}")
        } finally {
            this.stop()
        }
    }

    @Suppress("VulnerableCodeUsages")
    private object TestRequestHandler : AbstractHandler() {
        override fun handle(
            target: String,
            baseRequest: Request,
            request: HttpServletRequest,
            response: HttpServletResponse
        ) {
            val requestBody = request.inputStream.readBytes()
            response.status = 200
            response.setContentLength(requestBody.size)
            response.outputStream.write(requestBody)
            response.outputStream.close()
        }
    }
}
