rootProject.name = "internal-compatibility-matrix-tests"

pluginManagement {
    val kotlinDefaultVersion: String by extra
    val kotlinVersion = System.getenv("kotlinVersion") ?: kotlinDefaultVersion
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

//Web-Socket Servers
include("tests:websocket-servers:jetty-9.4")
include("tests:websocket-servers:jetty-10.0")
include("tests:websocket-servers:netty-4.1")
include("tests:websocket-servers:tomcat-9.0")
include("tests:websocket-servers:tomcat-10.1")
include("tests:websocket-servers:tomcat-11.0")
include("tests:websocket-servers:undertow-2.0")
include("tests:websocket-servers:undertow-2.3")

//Web-Socket Clients
include("tests:websocket-clients:jetty-9.4")
include("tests:websocket-clients:jetty-10.0")
include("tests:websocket-clients:netty-4.1")
include("tests:websocket-clients:tomcat-9.0")
include("tests:websocket-clients:tomcat-10.1")
include("tests:websocket-clients:tomcat-11.0")
include("tests:websocket-clients:undertow-2.0")
include("tests:websocket-clients:undertow-2.3")

//Web-Socket Per-Message Tests
include("tests:websocket-messages:undertow-2.0")
include("tests:websocket-messages:undertow-2.3")

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
//Apache CXF
include("tests:web-frameworks:cxf-3.4-jetty")
//Jersey
include("tests:web-frameworks:jersey-2-jetty")
//Spring MVC Web-Sockets
include("tests:websocket-servers-frameworks:spring-mvc-1.5-jetty")
include("tests:websocket-servers-frameworks:spring-mvc-1.5-tomcat")
include("tests:websocket-servers-frameworks:spring-mvc-1.5-undertow")
include("tests:websocket-servers-frameworks:spring-mvc-2.7-jetty")
include("tests:websocket-servers-frameworks:spring-mvc-2.7-tomcat")
include("tests:websocket-servers-frameworks:spring-mvc-2.7-undertow")
include("tests:websocket-servers-frameworks:spring-mvc-3.1-jetty")
include("tests:websocket-servers-frameworks:spring-mvc-3.1-tomcat")
include("tests:websocket-servers-frameworks:spring-mvc-3.1-undertow")
//Spring WebFlux Web-Sockets
include("tests:websocket-servers-frameworks:spring-webflux-2.7-jetty")
include("tests:websocket-servers-frameworks:spring-webflux-2.7-netty")
include("tests:websocket-servers-frameworks:spring-webflux-2.7-tomcat")
include("tests:websocket-servers-frameworks:spring-webflux-2.7-undertow")
include("tests:websocket-servers-frameworks:spring-webflux-3.1-jetty")
include("tests:websocket-servers-frameworks:spring-webflux-3.1-netty")
include("tests:websocket-servers-frameworks:spring-webflux-3.1-tomcat")
include("tests:websocket-servers-frameworks:spring-webflux-3.1-undertow")
//Spring MVC Web-Socket clients
include("tests:websocket-clients-frameworks:spring-mvc-1.5-jetty")
include("tests:websocket-clients-frameworks:spring-mvc-1.5-tomcat")
include("tests:websocket-clients-frameworks:spring-mvc-1.5-undertow")
include("tests:websocket-clients-frameworks:spring-mvc-2.7-jetty")
include("tests:websocket-clients-frameworks:spring-mvc-2.7-tomcat")
include("tests:websocket-clients-frameworks:spring-mvc-2.7-undertow")
include("tests:websocket-clients-frameworks:spring-mvc-3.1-jetty")
include("tests:websocket-clients-frameworks:spring-mvc-3.1-tomcat")
include("tests:websocket-clients-frameworks:spring-mvc-3.1-undertow")
//Spring MVC Web-Socket Per-Message Tests
include("tests:websocket-messages-frameworks:spring-mvc-1.5-undertow")
include("tests:websocket-messages-frameworks:spring-mvc-2.7-undertow")
include("tests:websocket-messages-frameworks:spring-mvc-3.1-undertow")
//Spring WebFlux Web-Socket clients
include("tests:websocket-clients-frameworks:spring-webflux-2.7-jetty")
include("tests:websocket-clients-frameworks:spring-webflux-2.7-netty")
include("tests:websocket-clients-frameworks:spring-webflux-2.7-tomcat")
include("tests:websocket-clients-frameworks:spring-webflux-2.7-undertow")
include("tests:websocket-clients-frameworks:spring-webflux-3.1-jetty")
include("tests:websocket-clients-frameworks:spring-webflux-3.1-netty")
include("tests:websocket-clients-frameworks:spring-webflux-3.1-tomcat")
include("tests:websocket-clients-frameworks:spring-webflux-3.1-undertow")

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

//Asynchronous Communication
//Service Executor
include("tests:async:executor-service")
//Reactor
include("tests:async:reactor-3.5")
include("tests:async:reactor-3.6")
//Spring Task Execution
include("tests:async:spring-task-execution-3.1")

include("common-test")
include("test-agent")