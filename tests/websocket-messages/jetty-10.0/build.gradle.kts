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
val logbackVersion: String by parent!!.extra
val serverVersion = "10.0.20"

dependencies {
    implementation("io.github.microutils:kotlin-logging-jvm:$microutilsLoggingVersion")
    implementation("org.eclipse.jetty:jetty-server:$serverVersion")
    implementation("org.eclipse.jetty.websocket:websocket-javax-server:$serverVersion")
    implementation("org.eclipse.jetty.websocket:websocket-javax-client:$serverVersion")
    testImplementation(kotlin("test-junit"))
    testImplementation(project(":common-test"))
}

license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    include("**/*.kt")
    include("**/*.java")
    include("**/*.groovy")
}
