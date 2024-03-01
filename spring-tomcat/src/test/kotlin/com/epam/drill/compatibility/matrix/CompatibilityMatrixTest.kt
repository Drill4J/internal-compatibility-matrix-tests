package com.epam.drill.compatibility.matrix


import java.util.logging.LogManager
import org.apache.catalina.startup.Tomcat
import mu.KotlinLogging
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CompatibilityMatrixTest : AbstractCompatibilityMatrixTest() {
    override val logger = KotlinLogging.logger {}

    override fun withHttpServer(block: (String) -> Unit) = Tomcat().run {
        try {
            LogManager.getLogManager().readConfiguration(ClassLoader.getSystemResourceAsStream("logging.properties"))
            this.setBaseDir("./build")
            this.setPort(0)
            val context = this.addContext("", null)
            this.addServlet(context.path, TestRequestServlet::class.simpleName, TestRequestServlet())
            context.addServletMappingDecoded("/", TestRequestServlet::class.simpleName)
            this.start()
            block("http://localhost:${connector.localPort}")
        } finally {
            this.stop()
        }
    }

    private class TestRequestServlet : HttpServlet() {
        override fun doPost(request: HttpServletRequest, response: HttpServletResponse) {
            val requestBody = request.inputStream.readBytes()
            response.status = 200
            response.setContentLength(requestBody.size)
            response.outputStream.write(requestBody)
            response.outputStream.close()
        }
    }
}
