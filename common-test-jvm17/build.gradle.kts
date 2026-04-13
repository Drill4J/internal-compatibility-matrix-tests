import java.net.URI

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.hierynomus.license")
}

kotlin {
    jvmToolchain(17)
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    api(project(":common-test"))

    compileOnly("org.apache.kafka:kafka-clients:3.7.2")
    compileOnly("org.springframework.kafka:spring-kafka:3.1.4")

    compileOnly(kotlin("test-junit"))
}

license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    include("**/*.kt")
    include("**/*.java")
    include("**/*.groovy")
}

