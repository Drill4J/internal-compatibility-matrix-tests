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
    testImplementation(kotlin("test-junit"))
    testImplementation(project(":common-test"))
    testImplementation("org.glassfish.tyrus:tyrus-client:1.20")
    testImplementation("org.glassfish.tyrus:tyrus-container-grizzly-client:1.20")
}

license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    include("**/*.kt")
    include("**/*.java")
    include("**/*.groovy")
}
