rootProject.name = "internal-compatibility-matrix-tests"

pluginManagement {
    val kotlinVersion = System.getenv("kotlinVersion") ?: "1.9.20"
    val kotlinMultiplatformVersion: String by extra
    val shadowPluginVersion: String by extra
    val licenseVersion: String by extra
    val psxpaulExecforkVersion: String by extra
    val drillCiCdIntegrationVersion: String by extra

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion

        id("com.github.johnrengelman.shadow") version shadowPluginVersion
        id("com.github.hierynomus.license") version licenseVersion
        id("com.github.psxpaul.execfork") version psxpaulExecforkVersion
        id("com.epam.drill.integration.cicd") version drillCiCdIntegrationVersion
        id("test-report-aggregation")
    }

    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

fun includeIfSupport(projectPath: String, javaVersions: IntRange, os: List<String> = emptyList()) {
    val currentOS = System.getProperty("os.name")
    if (os.isNotEmpty() && !os.any { currentOS.toLowerCase().contains(it.toLowerCase()) }) {
        logger.lifecycle("Project :$projectPath is not included as OS \"$currentOS\" is not $os")
        return
    }
    val currentJavaVersion = System.getProperty("java.version")
        .split(".")
        .let {
            if (it[0] == "1") it[1].toInt() else it[0].toInt()
        }
    if (currentJavaVersion !in javaVersions) {
        logger.lifecycle("Project :$projectPath is not included as Java $currentJavaVersion is not $javaVersions")
        return
    }

    include(projectPath)
}

val maxJavaVersion = 21
val windows = "Windows"
val linux = "Linux"
val macos = "Mac OS"

include("common-test")
include("stub-server")

//Tests
//Web Servers
//includeIfSupport("tests:web-servers:netty-4.1", 8..maxJavaVersion) TODO Fails with java.lang.AssertionError: actual value is not null expected null, but was:<session-123>
includeIfSupport("tests:web-servers:jetty-10.0", 11..maxJavaVersion)
includeIfSupport("tests:web-servers:tomcat-10.1", 11..maxJavaVersion)
includeIfSupport("tests:web-servers:tomcat-11.0", 17..maxJavaVersion)
includeIfSupport("tests:web-servers:undertow-2.3", 11..maxJavaVersion)

//Web Frameworks
//Http Servlets
includeIfSupport("tests:web-frameworks:servlet-4-tomcat-9", 8..maxJavaVersion)
includeIfSupport("tests:web-frameworks:servlet-4-jetty-9", 8..maxJavaVersion)
includeIfSupport("tests:web-frameworks:servlet-5-tomcat-10", 11..maxJavaVersion)
includeIfSupport("tests:web-frameworks:servlet-5-jetty-11", 17..maxJavaVersion)
includeIfSupport("tests:web-frameworks:servlet-5-undertow-2", 11..maxJavaVersion)
//includeIfSupport("tests:web-frameworks:servlet-5-glassfish-7", 11..maxJavaVersion) TODO Fails with JDWP Transport dt_socket failed to initialize
includeIfSupport("tests:web-frameworks:servlet-6-tomcat-11", 17..maxJavaVersion)
//Spring MVC
includeIfSupport("tests:web-frameworks:spring-mvc-1.5-jetty", 8..17)
includeIfSupport("tests:web-frameworks:spring-mvc-1.5-tomcat", 8..17)
includeIfSupport("tests:web-frameworks:spring-mvc-1.5-undertow", 8..17)
includeIfSupport("tests:web-frameworks:spring-mvc-2.7-jetty", 8..maxJavaVersion)
includeIfSupport("tests:web-frameworks:spring-mvc-2.7-tomcat", 8..maxJavaVersion)
includeIfSupport("tests:web-frameworks:spring-mvc-2.7-undertow", 8..maxJavaVersion)
includeIfSupport("tests:web-frameworks:spring-mvc-3.1-jetty", 17..maxJavaVersion)
includeIfSupport("tests:web-frameworks:spring-mvc-3.1-tomcat", 17..maxJavaVersion)
includeIfSupport("tests:web-frameworks:spring-mvc-3.1-undertow", 17..maxJavaVersion)
//Spring WebFlux
includeIfSupport("tests:web-frameworks:spring-webflux-2.7-jetty", 8..maxJavaVersion)
includeIfSupport("tests:web-frameworks:spring-webflux-2.7-netty", 8..maxJavaVersion)
includeIfSupport("tests:web-frameworks:spring-webflux-2.7-tomcat", 8..maxJavaVersion)
includeIfSupport("tests:web-frameworks:spring-webflux-2.7-undertow", 8..maxJavaVersion)
includeIfSupport("tests:web-frameworks:spring-webflux-3.1-jetty", 17..maxJavaVersion)
includeIfSupport("tests:web-frameworks:spring-webflux-3.1-netty", 17..maxJavaVersion)
includeIfSupport("tests:web-frameworks:spring-webflux-3.1-tomcat", 17..maxJavaVersion)
includeIfSupport("tests:web-frameworks:spring-webflux-3.1-undertow", 17..maxJavaVersion)
//Apache CXF
includeIfSupport("tests:web-frameworks:cxf-3.4-jetty", 8..maxJavaVersion)
//Jersey
includeIfSupport("tests:web-frameworks:jersey-2-jetty", 8..17)

//Http Clients
//URLConnection
includeIfSupport("tests:http-clients:urlconnection", 8..maxJavaVersion)
//Apache HttpClient
includeIfSupport("tests:http-clients:apache-http-client-4.5", 8..maxJavaVersion)
//includeIfSupport("tests:http-clients:apache-http-client-5.3", 8..maxJavaVersion) TODO Fails with java.lang.AssertionError: expected:<session-123-returned> but was:<null>
//OkHttp Client
includeIfSupport("tests:http-clients:okhttp-client-3.12", 8..maxJavaVersion)
includeIfSupport("tests:http-clients:okhttp-client-3.14", 8..maxJavaVersion)
includeIfSupport("tests:http-clients:okhttp-client-4.12", 8..maxJavaVersion)
//Spring RestTemplate
includeIfSupport("tests:http-clients:spring-resttemplate-4.3", 8..maxJavaVersion)
includeIfSupport("tests:http-clients:spring-resttemplate-5.3", 8..maxJavaVersion)
//Spring WebClient
includeIfSupport("tests:http-clients:spring-webclient-5.3", 8..maxJavaVersion)
includeIfSupport("tests:http-clients:spring-webclient-6.1", 17..maxJavaVersion)
//Feign Client
includeIfSupport("tests:http-clients:feign-client-13", 8..maxJavaVersion)

//Asynchronous Communication
//Service Executor
includeIfSupport("tests:async:executor-service", 8..maxJavaVersion)
//Reactor
includeIfSupport("tests:async:reactor-3.5", 8..maxJavaVersion)
includeIfSupport("tests:async:reactor-3.6", 8..maxJavaVersion)
//Spring Task Execution
//includeIfSupport("tests:async:spring-task-execution-3.1", 17..maxJavaVersion) TODO Fails with org.junit.ComparisonFailure: expected:<test-[session-123]> but was:<test-[null]>

//Test Frameworks
//JUnit
includeIfSupport("tests:test-frameworks:junit-4", 8..17)
includeIfSupport("tests:test-frameworks:junit-5", 8..17)
//TestNG
includeIfSupport("tests:test-frameworks:testng-6.1", 8..17)
includeIfSupport("tests:test-frameworks:testng-7.4", 8..17)
//Selenium
includeIfSupport("tests:test-frameworks:selenium-4", 11..17, listOf(linux))
//Rest Assured
includeIfSupport("tests:test-frameworks:rest-assured-5.3", 8..17)

//Spring MVC Web-Sockets
includeIfSupport("tests:websocket-servers-frameworks:spring-mvc-1.5-jetty", 8..17)
includeIfSupport("tests:websocket-servers-frameworks:spring-mvc-1.5-tomcat", 8..17)
includeIfSupport("tests:websocket-servers-frameworks:spring-mvc-1.5-undertow", 8..17)
includeIfSupport("tests:websocket-servers-frameworks:spring-mvc-2.7-jetty", 8..maxJavaVersion)
includeIfSupport("tests:websocket-servers-frameworks:spring-mvc-2.7-tomcat", 8..maxJavaVersion)
includeIfSupport("tests:websocket-servers-frameworks:spring-mvc-2.7-undertow", 8..maxJavaVersion)
includeIfSupport("tests:websocket-servers-frameworks:spring-mvc-3.1-jetty", 17..maxJavaVersion)
includeIfSupport("tests:websocket-servers-frameworks:spring-mvc-3.1-tomcat", 17..maxJavaVersion)
includeIfSupport("tests:websocket-servers-frameworks:spring-mvc-3.1-undertow", 17..maxJavaVersion)
////Spring WebFlux Web-Sockets
includeIfSupport("tests:websocket-servers-frameworks:spring-webflux-2.7-jetty", 8..maxJavaVersion)
includeIfSupport("tests:websocket-servers-frameworks:spring-webflux-2.7-netty", 8..maxJavaVersion)
includeIfSupport("tests:websocket-servers-frameworks:spring-webflux-2.7-tomcat", 8..maxJavaVersion)
includeIfSupport("tests:websocket-servers-frameworks:spring-webflux-2.7-undertow", 8..maxJavaVersion)
includeIfSupport("tests:websocket-servers-frameworks:spring-webflux-3.1-jetty", 17..maxJavaVersion)
includeIfSupport("tests:websocket-servers-frameworks:spring-webflux-3.1-netty", 17..maxJavaVersion)
includeIfSupport("tests:websocket-servers-frameworks:spring-webflux-3.1-tomcat", 17..maxJavaVersion)
includeIfSupport("tests:websocket-servers-frameworks:spring-webflux-3.1-undertow", 17..maxJavaVersion)
////Spring MVC Web-Socket clients
includeIfSupport("tests:websocket-clients-frameworks:spring-mvc-1.5-jetty", 8..17)
includeIfSupport("tests:websocket-clients-frameworks:spring-mvc-1.5-tomcat", 8..17)
includeIfSupport("tests:websocket-clients-frameworks:spring-mvc-1.5-undertow", 8..17)
includeIfSupport("tests:websocket-clients-frameworks:spring-mvc-2.7-jetty", 8..maxJavaVersion)
includeIfSupport("tests:websocket-clients-frameworks:spring-mvc-2.7-tomcat", 8..maxJavaVersion)
includeIfSupport("tests:websocket-clients-frameworks:spring-mvc-2.7-undertow", 8..maxJavaVersion)
includeIfSupport("tests:websocket-clients-frameworks:spring-mvc-3.1-jetty", 17..maxJavaVersion)
includeIfSupport("tests:websocket-clients-frameworks:spring-mvc-3.1-tomcat", 17..maxJavaVersion)
includeIfSupport("tests:websocket-clients-frameworks:spring-mvc-3.1-undertow", 17..maxJavaVersion)
////Spring WebFlux Web-Socket clients
includeIfSupport("tests:websocket-clients-frameworks:spring-webflux-2.7-jetty", 8..maxJavaVersion)
includeIfSupport("tests:websocket-clients-frameworks:spring-webflux-2.7-netty", 8..maxJavaVersion)
includeIfSupport("tests:websocket-clients-frameworks:spring-webflux-2.7-tomcat", 8..maxJavaVersion)
includeIfSupport("tests:websocket-clients-frameworks:spring-webflux-2.7-undertow", 8..maxJavaVersion)
includeIfSupport("tests:websocket-clients-frameworks:spring-webflux-3.1-jetty", 17..maxJavaVersion)
includeIfSupport("tests:websocket-clients-frameworks:spring-webflux-3.1-netty", 17..maxJavaVersion)
includeIfSupport("tests:websocket-clients-frameworks:spring-webflux-3.1-tomcat", 17..maxJavaVersion)
includeIfSupport("tests:websocket-clients-frameworks:spring-webflux-3.1-undertow", 17..maxJavaVersion)
////Spring MVC Web-Socket Per-Message Tests
//include("tests:websocket-messages-frameworks:spring-mvc-1.5-jetty")
//include("tests:websocket-messages-frameworks:spring-mvc-1.5-tomcat")
//include("tests:websocket-messages-frameworks:spring-mvc-1.5-undertow")
//include("tests:websocket-messages-frameworks:spring-mvc-2.7-jetty")
//include("tests:websocket-messages-frameworks:spring-mvc-2.7-tomcat")
//include("tests:websocket-messages-frameworks:spring-mvc-2.7-undertow")
//include("tests:websocket-messages-frameworks:spring-mvc-3.1-jetty")
//include("tests:websocket-messages-frameworks:spring-mvc-3.1-tomcat")
//include("tests:websocket-messages-frameworks:spring-mvc-3.1-undertow")
////Spring WebFlux Web-Socket Per-Message Tests
//include("tests:websocket-messages-frameworks:spring-webflux-2.7-jetty")
//include("tests:websocket-messages-frameworks:spring-webflux-2.7-netty")
//include("tests:websocket-messages-frameworks:spring-webflux-2.7-tomcat")
//include("tests:websocket-messages-frameworks:spring-webflux-2.7-undertow")
//include("tests:websocket-messages-frameworks:spring-webflux-3.1-jetty")
//include("tests:websocket-messages-frameworks:spring-webflux-3.1-netty")
//include("tests:websocket-messages-frameworks:spring-webflux-3.1-tomcat")
//include("tests:websocket-messages-frameworks:spring-webflux-3.1-undertow")