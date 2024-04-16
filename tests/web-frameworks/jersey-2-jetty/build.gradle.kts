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
val jerseyVersion = "2.42"

dependencies {
    implementation("io.github.microutils:kotlin-logging-jvm:$microutilsLoggingVersion")
    implementation("org.glassfish.jersey.core:jersey-server:$jerseyVersion")
    implementation("org.glassfish.jersey.inject:jersey-hk2:$jerseyVersion")
    implementation("org.glassfish.jersey.containers:jersey-container-jetty-http:$jerseyVersion")
    testImplementation(kotlin("test-junit"))
    testImplementation(project(":common-test"))
}

license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    include("**/*.kt")
    include("**/*.java")
    include("**/*.groovy")
}
