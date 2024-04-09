import java.net.URI

plugins {
    kotlin("jvm")
    id("com.github.hierynomus.license")
}

version = rootProject.version
group = rootProject.group

repositories {
    mavenCentral()
}

val nativeAgentLibName: String by parent!!.extra
val microutilsLoggingVersion: String by parent!!.extra
val springBootVersion = "3.1.9"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux:$springBootVersion") {
        // We should have spring-boot-starter-reactor-netty module due to WebClient bean
        //      exclude(group = "org.springframework.boot", module = "spring-boot-starter-reactor-netty")
    }
    implementation("org.springframework.boot:spring-boot-starter-undertow:$springBootVersion")
    implementation("io.github.microutils:kotlin-logging-jvm:$microutilsLoggingVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testImplementation(kotlin("test-junit"))
    testImplementation("io.projectreactor:reactor-test:3.4.10")
    testImplementation(project(":common-test"))
}

license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    include("**/*.kt")
    include("**/*.java")
    include("**/*.groovy")
}
