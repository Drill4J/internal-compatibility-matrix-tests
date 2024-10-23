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

val microutilsLoggingVersion: String by parent!!.extra
val serverVersion = "4.1.107.Final"

dependencies {
    implementation("io.github.microutils:kotlin-logging-jvm:$microutilsLoggingVersion")
    implementation("io.netty:netty-codec-http:$serverVersion")
    testImplementation(kotlin("test-junit"))
    testImplementation(project(":common-test"))
    testImplementation("org.mockito:mockito-core:4.5.1")
    testImplementation("org.glassfish.tyrus:tyrus-server:1.20")
    testImplementation("org.glassfish.tyrus:tyrus-container-grizzly-server:1.20")
}

license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    include("**/*.kt")
    include("**/*.java")
    include("**/*.groovy")
}
