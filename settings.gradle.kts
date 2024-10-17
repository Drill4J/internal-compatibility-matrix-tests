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

fun includeForJavaVersion(projectPath: String, javaVersions: IntRange) {
    val currentJavaVersion = System.getProperty("java.version")
        .split(".")
        .let {
            if (it[0] == "1") it[1].toInt() else it[0].toInt()
        }
    if (currentJavaVersion in javaVersions)
        include(projectPath)
    else
        logger.lifecycle("Project :$projectPath is not included as Java is not $javaVersions")
}

val maxJavaVersion = 21

include("common-test")
include("stub-server")

//Tests
//Web Servers
includeForJavaVersion("tests:web-servers:netty-4.1", 8..maxJavaVersion)
includeForJavaVersion("tests:web-servers:jetty-10.0", 11..maxJavaVersion)
includeForJavaVersion("tests:web-servers:tomcat-10.1", 11..maxJavaVersion)
includeForJavaVersion("tests:web-servers:tomcat-11.0", 17..maxJavaVersion)
includeForJavaVersion("tests:web-servers:undertow-2.3", 11..maxJavaVersion)

//Web Frameworks
//Http Servlets
//includeForJavaVersion("tests:web-frameworks:servlet-4-tomcat-9", 8..maxJavaVersion)
//includeForJavaVersion("tests:web-frameworks:servlet-4-jetty-9", 8..maxJavaVersion)
//includeForJavaVersion("tests:web-frameworks:servlet-5-tomcat-10", 11..maxJavaVersion)
//includeForJavaVersion("tests:web-frameworks:servlet-5-jetty-11", 17..maxJavaVersion)
//includeForJavaVersion("tests:web-frameworks:servlet-5-undertow-2", 11..maxJavaVersion)
//includeForJavaVersion("tests:web-frameworks:servlet-5-glassfish-7", 8..maxJavaVersion)
//includeForJavaVersion("tests:web-frameworks:servlet-6-tomcat-11", 17..maxJavaVersion)
//Spring MVC
//includeForJavaVersion("tests:web-frameworks:spring-mvc-1.5-jetty", 8..17)
//includeForJavaVersion("tests:web-frameworks:spring-mvc-1.5-tomcat", 8..17)
//includeForJavaVersion("tests:web-frameworks:spring-mvc-1.5-undertow", 8..17)
//includeForJavaVersion("tests:web-frameworks:spring-mvc-2.7-jetty", 8..maxJavaVersion)
//includeForJavaVersion("tests:web-frameworks:spring-mvc-2.7-tomcat", 8..maxJavaVersion)
//includeForJavaVersion("tests:web-frameworks:spring-mvc-2.7-undertow", 8..maxJavaVersion)
//includeForJavaVersion("tests:web-frameworks:spring-mvc-3.1-jetty", 17..maxJavaVersion)
//includeForJavaVersion("tests:web-frameworks:spring-mvc-3.1-tomcat", 17..maxJavaVersion)
//includeForJavaVersion("tests:web-frameworks:spring-mvc-3.1-undertow", 17..maxJavaVersion)
////Spring WebFlux
//includeForJavaVersion("tests:web-frameworks:spring-webflux-2.7-jetty", 8..maxJavaVersion)
//includeForJavaVersion("tests:web-frameworks:spring-webflux-2.7-netty", 8..maxJavaVersion)
//includeForJavaVersion("tests:web-frameworks:spring-webflux-2.7-tomcat", 8..maxJavaVersion)
//includeForJavaVersion("tests:web-frameworks:spring-webflux-2.7-undertow", 8..maxJavaVersion)
//includeForJavaVersion("tests:web-frameworks:spring-webflux-3.1-jetty", 17..maxJavaVersion)
//includeForJavaVersion("tests:web-frameworks:spring-webflux-3.1-netty", 17..maxJavaVersion)
//includeForJavaVersion("tests:web-frameworks:spring-webflux-3.1-tomcat", 17..maxJavaVersion)
//includeForJavaVersion("tests:web-frameworks:spring-webflux-3.1-undertow", 17..maxJavaVersion)
////Apache CXF
//includeForJavaVersion("tests:web-frameworks:cxf-3.4-jetty", 8..maxJavaVersion)
////Jersey
//includeForJavaVersion("tests:web-frameworks:jersey-2-jetty", 8..17)
//
////Http Clients
////URLConnection
//includeForJavaVersion("tests:http-clients:urlconnection", 8..maxJavaVersion)
////Apache HttpClient
//includeForJavaVersion("tests:http-clients:apache-http-client-4.5", 8..maxJavaVersion)
//includeForJavaVersion("tests:http-clients:apache-http-client-5.3", 8..maxJavaVersion)
////OkHttp Client
//includeForJavaVersion("tests:http-clients:okhttp-client-3.12", 8..maxJavaVersion)
//includeForJavaVersion("tests:http-clients:okhttp-client-3.14", 8..maxJavaVersion)
//includeForJavaVersion("tests:http-clients:okhttp-client-4.12", 8..maxJavaVersion)
////Spring RestTemplate
//includeForJavaVersion("tests:http-clients:spring-resttemplate-4.3", 8..maxJavaVersion)
//includeForJavaVersion("tests:http-clients:spring-resttemplate-5.3", 8..maxJavaVersion)
////Spring WebClient
//includeForJavaVersion("tests:http-clients:spring-webclient-5.3", 8..maxJavaVersion)
//includeForJavaVersion("tests:http-clients:spring-webclient-6.1", 17..maxJavaVersion)
////Feign Client
//includeForJavaVersion("tests:http-clients:feign-client-13", 8..maxJavaVersion)
//
////Asynchronous Communication
////Service Executor
//includeForJavaVersion("tests:async:executor-service", 8..maxJavaVersion)
////Reactor
//includeForJavaVersion("tests:async:reactor-3.5", 8..maxJavaVersion)
//includeForJavaVersion("tests:async:reactor-3.6", 8..maxJavaVersion)
////Spring Task Execution
//includeForJavaVersion("tests:async:spring-task-execution-3.1", 17..maxJavaVersion)
//
////Test Frameworks
////JUnit
//includeForJavaVersion("tests:test-frameworks:junit-4", 8..maxJavaVersion)
//includeForJavaVersion("tests:test-frameworks:junit-5", 8..maxJavaVersion)
////TestNG
//includeForJavaVersion("tests:test-frameworks:testng-6.1", 8..maxJavaVersion)
//includeForJavaVersion("tests:test-frameworks:testng-7.4", 8..maxJavaVersion)
////Selenium
//includeForJavaVersion("tests:test-frameworks:selenium-4", 8..maxJavaVersion)
////Rest Assured
//includeForJavaVersion("tests:test-frameworks:rest-assured-5.3", 8..maxJavaVersion)
