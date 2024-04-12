rootProject.name = "internal-compatibility-matrix-tests"

pluginManagement {
    val kotlinVersion = System.getenv("kotlinVersion") ?: "1.6.0"
    val kotlinMultiplatformVersion: String by extra
    val shadowPluginVersion: String by extra
    val licenseVersion: String by extra

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("multiplatform") version kotlinMultiplatformVersion
        kotlin("plugin.serialization") version kotlinVersion

        id("com.github.johnrengelman.shadow") version shadowPluginVersion
        id("com.github.hierynomus.license") version licenseVersion
    }

    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

val sharedLibsLocalPath: String by extra
val includeSharedLib: Settings.(String) -> Unit = {
    include(it)
    project(":$it").projectDir = file(sharedLibsLocalPath).resolve(it)
}

includeSharedLib("logging-native")
includeSharedLib("knasm")
includeSharedLib("jvmapi")
includeSharedLib("common")
includeSharedLib("logging")
includeSharedLib("agent-instrumentation")

//Tests
//Web Servers
include("tests:web-servers:jetty-10.0")
include("tests:web-servers:netty-4.1")
include("tests:web-servers:tomcat-11.0")
include("tests:web-servers:tomcat-10.1")
include("tests:web-servers:undertow-2.3")

//Web Frameworks
//Http Servlets
include("tests:web-frameworks:servlet-4-tomcat-9")
include("tests:web-frameworks:servlet-4-jetty-9")
include("tests:web-frameworks:servlet-5-tomcat-10")
include("tests:web-frameworks:servlet-5-jetty-11")
include("tests:web-frameworks:servlet-5-undertow-2")
include("tests:web-frameworks:servlet-5-glassfish-7")
include("tests:web-frameworks:servlet-6-tomcat-11")
//Spring MVC
include("tests:web-frameworks:spring-mvc-1.5-jetty")
include("tests:web-frameworks:spring-mvc-1.5-tomcat")
include("tests:web-frameworks:spring-mvc-1.5-undertow")
include("tests:web-frameworks:spring-mvc-2.7-jetty")
include("tests:web-frameworks:spring-mvc-2.7-tomcat")
include("tests:web-frameworks:spring-mvc-2.7-undertow")
include("tests:web-frameworks:spring-mvc-3.1-jetty")
include("tests:web-frameworks:spring-mvc-3.1-tomcat")
include("tests:web-frameworks:spring-mvc-3.1-undertow")
//Spring WebFlux
include("tests:web-frameworks:spring-webflux-2.7-jetty")
include("tests:web-frameworks:spring-webflux-2.7-netty")
include("tests:web-frameworks:spring-webflux-2.7-tomcat")
include("tests:web-frameworks:spring-webflux-2.7-undertow")
include("tests:web-frameworks:spring-webflux-3.1-jetty")
include("tests:web-frameworks:spring-webflux-3.1-netty")
include("tests:web-frameworks:spring-webflux-3.1-tomcat")
include("tests:web-frameworks:spring-webflux-3.1-undertow")

//Http Clients
//URLConnection
include("tests:http-clients:urlconnection")
//Apache HttpClient
include("tests:http-clients:apache-http-client-4.5")
include("tests:http-clients:apache-http-client-5.3")
//OkHttp Client
include("tests:http-clients:okhttp-client-3.12")
include("tests:http-clients:okhttp-client-3.14")
include("tests:http-clients:okhttp-client-4.12")
//Spring RestTemplate
include("tests:http-clients:spring-resttemplate-4.3")
include("tests:http-clients:spring-resttemplate-5.3")
//Spring WebClient
include("tests:http-clients:spring-webclient-5.3")
include("tests:http-clients:spring-webclient-6.1")
//Feign Client
include("tests:http-clients:feign-client-13")

include("common-test")
include("test-agent")