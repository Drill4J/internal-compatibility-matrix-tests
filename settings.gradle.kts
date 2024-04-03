rootProject.name = "internal-compatibility-matrix-tests"

pluginManagement {
    val kotlinVersion: String by extra
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
include("clean-jetty-10.0.20")
include("clean-netty-4.1.107")
include("clean-tomcat-11.0.0-M18")
include("clean-tomcat-10.1.19")
include("clean-undertow-2.3.12")
include("spring-1.5.22-jetty")
include("spring-1.5.22-tomcat")
include("spring-1.5.22-undertow")
include("spring-2.7.18-jetty")
include("spring-2.7.18-tomcat")
include("spring-2.7.18-undertow")
include("spring-2.7.18-webflux-jetty")
include("spring-2.7.18-webflux-netty")
include("spring-2.7.18-webflux-tomcat")
include("spring-2.7.18-webflux-undertow")
include("spring-3.1.9-jetty")
include("spring-3.1.9-tomcat")
include("spring-3.1.9-undertow")
include("spring-3.1.9-webflux-jetty")
include("spring-3.1.9-webflux-netty")
include("spring-3.1.9-webflux-tomcat")
include("spring-3.1.9-webflux-undertow")
include("common-test")
include("test-agent")
include("servlet-4-tomcat-9")
include("servlet-5-tomcat-10")
include("servlet-6-tomcat-11")
