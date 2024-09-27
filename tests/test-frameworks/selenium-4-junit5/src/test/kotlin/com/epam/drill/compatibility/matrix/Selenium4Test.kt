package com.epam.drill.compatibility.matrix

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockserver.client.MockServerClient
import org.mockserver.integration.ClientAndServer.startClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MockServerContainer.PORT
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class Selenium4Test {
    private val debuggerPort = 9233
    private lateinit var driver: WebDriver
    private lateinit var mockServerClient: MockServerClient

    @Container
    private val devtoolsProxy: GenericContainer<Nothing> = GenericContainer<Nothing>("drill4j/devtools-proxy:0.1.0")
        .apply {
            withExposedPorts(8080)
            portBindings = listOf("8093:8080")
        }

    @BeforeEach
    fun setUp() {
        mockServerClient = startClientAndServer(PORT)

        mockServerClient.`when`(
            HttpRequest.request().withMethod("GET").withPath("/")
        ).respond(
            HttpResponse.response().withStatusCode(200).withBody("Mock server response")
        )

        org.testcontainers.Testcontainers.exposeHostPorts(debuggerPort)
    }

    @AfterEach
    fun tearDown() {
        driver.quit()
        mockServerClient.stop()
    }

    @Test
    fun `given Chrome 127 version, Selenium transformer should add Drill headers to requests`() {
        driver = ChromeDriver(ChromeOptions().apply {
            addArguments("--remote-debugging-port=$debuggerPort")
            addArguments("--disable-search-engine-choice-screen")
            addArguments("--headless")
            setCapability("browserVersion", "127")
        })

        driver.get("http://localhost:$PORT/")

        val requests = mockServerClient.retrieveRecordedRequests(
            HttpRequest.request().withMethod("GET")
                .withHeader("drill-test-id")
        )
        assertTrue(requests.isNotEmpty(), "MockServer did not receive a request with header 'drill-test-id'")
    }

    @Test
    fun `given Chrome latest version, Selenium transformer should add Drill headers to requests`() {
        driver = ChromeDriver(ChromeOptions().apply {
            addArguments("--remote-debugging-port=$debuggerPort")
            addArguments("--disable-search-engine-choice-screen")
            addArguments("--headless")
        })

        driver.get("http://localhost:$PORT/")

        val requests = mockServerClient.retrieveRecordedRequests(
            HttpRequest.request().withMethod("GET")
                .withHeader("drill-test-id")
        )
        assertTrue(requests.isNotEmpty(), "MockServer did not receive a request with header 'drill-test-id'")
    }
}