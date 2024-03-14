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
include("spring-2.5.5-jetty")
include("spring-2.5.5-tomcat")
include("spring-2.5.5-undertow")
include("spring-2.5.5-webflux-jetty")
include("spring-2.5.5-webflux-netty")
include("spring-2.5.5-webflux-tomcat")
include("spring-2.5.5-webflux-undertow")
include("spring-2.7.18-jetty")
include("spring-2.7.18-tomcat")
include("spring-2.7.18-undertow")
include("spring-2.7.18-webflux-jetty")
include("spring-2.7.18-webflux-netty")
include("spring-2.7.18-webflux-tomcat")
include("spring-2.7.18-webflux-undertow")
include("common-test")
include("test-agent")
