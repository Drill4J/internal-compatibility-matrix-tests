import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.github.hierynomus.license")
}

group = rootProject.group
version = rootProject.version

val microutilsLoggingVersion: String by parent!!.extra
val kotlinxSerializationVersion: String by parent!!.extra

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("javax.websocket:javax.websocket-api:1.1")
    compileOnly("jakarta.websocket:jakarta.websocket-api:2.1.1")
    compileOnly("jakarta.websocket:jakarta.websocket-client-api:2.1.1")
    compileOnly("org.springframework:spring-test:5.3.31")
    compileOnly("org.springframework:spring-beans:5.3.31")
    compileOnly("org.springframework:spring-web:5.3.31")
    compileOnly("org.springframework:spring-websocket:5.3.31")
    compileOnly("org.springframework:spring-webflux:5.3.31")
    compileOnly("org.springframework:spring-context:5.3.31")
    compileOnly("org.springframework.boot:spring-boot-starter-test:2.7.18")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure:2.7.18")
    compileOnly("io.github.microutils:kotlin-logging-jvm:$microutilsLoggingVersion")
    compileOnly("io.projectreactor:reactor-core:3.4.34")
    compileOnly("org.simpleframework:simple-http:6.0.1")
    compileOnly("org.glassfish.tyrus:tyrus-client:1.20")
    compileOnly("org.glassfish.tyrus:tyrus-server:1.20")
    compileOnly("org.glassfish.tyrus:tyrus-container-grizzly-client:1.20")
    compileOnly("org.glassfish.tyrus:tyrus-container-grizzly-server:1.20")

    compileOnly(kotlin("test-junit"))

    api("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.current().toString()
}

license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    include("**/*.kt")
    include("**/*.java")
    include("**/*.groovy")
}
