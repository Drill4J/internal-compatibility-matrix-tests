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
val springBootVersion = "1.5.22.RELEASE"

dependencies {
    implementation("io.github.microutils:kotlin-logging-jvm:$microutilsLoggingVersion")
    implementation("org.springframework.boot:spring-boot-starter-undertow:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-websocket:$springBootVersion") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    testImplementation(kotlin("test-junit"))
    testImplementation(project(":common-test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testImplementation("org.glassfish.tyrus:tyrus-client:1.20")
    testImplementation("org.glassfish.tyrus:tyrus-container-grizzly-client:1.20")
}

license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    include("**/*.kt")
    include("**/*.java")
    include("**/*.groovy")
}
