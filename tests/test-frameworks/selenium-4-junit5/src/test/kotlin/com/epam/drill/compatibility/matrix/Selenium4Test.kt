package com.epam.drill.compatibility.matrix

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockserver.client.MockServerClient
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import org.testcontainers.containers.BrowserWebDriverContainer
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.containers.MockServerContainer.PORT
import org.testcontainers.containers.Network
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.net.URL


@Testcontainers
class Selenium4Test {

    private lateinit var driver: WebDriver
    private lateinit var mockServerClient: MockServerClient

    @Container
    private val mockServer: MockServerContainer = MockServerContainer(
        DockerImageName.parse("mockserver/mockserver:5.15.0")
    ).apply {
        withNetworkAliases("mockserver")
    }

    @Container
    private val chrome: BrowserWebDriverContainer<Nothing> =
        BrowserWebDriverContainer<Nothing>("selenium/standalone-chrome:112.0")
            .withCapabilities(ChromeOptions())

    @Container
    private val devtoolsProxy: GenericContainer<Nothing> = GenericContainer<Nothing>("drill4j/devtools-proxy:0.1.0")
        .apply {
            withExposedPorts(8080)
        }

    @BeforeEach
    fun setUp() {
        mockServerClient = MockServerClient(mockServer.host, mockServer.serverPort)

        mockServerClient.`when`(
            HttpRequest.request().withMethod("GET").withPath("/")
        ).respond(
            HttpResponse.response().withStatusCode(200).withBody("Mock server response")
        )
        println("Mock Server: http://${mockServer.host}:${mockServer.serverPort}")
        println("Chrome Container: ${chrome.seleniumAddress}")
        println("Devtools Proxy: http://${devtoolsProxy.host}:${devtoolsProxy.firstMappedPort}")

        driver = RemoteWebDriver(URL(chrome.seleniumAddress.toString()), ChromeOptions())
            .apply {
//                manage().timeouts().pageLoadTimeout(Duration.ofSeconds(50))
            }
    }

    @AfterEach
    fun tearDown() {
        driver.quit()
        mockServerClient.stop()
    }

    @Test
    fun testSeleniumWithMockServer() {

        driver.get("http://host.testcontainers.internal:${PORT}/")

        val requests = mockServerClient.retrieveRecordedRequests(
            HttpRequest.request().withMethod("GET").withHeader("drill-test-id")
        )

        assertTrue(requests.isNotEmpty(), "MockServer did not receive a request with header 'drill-test-id'")
    }
}