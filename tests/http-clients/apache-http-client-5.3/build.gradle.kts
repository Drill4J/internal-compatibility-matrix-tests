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

dependencies {
    implementation("io.github.microutils:kotlin-logging-jvm:$microutilsLoggingVersion")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.3.1")
    testImplementation("org.simpleframework:simple-http:6.0.1")
    testImplementation(kotlin("test-junit"))
    testImplementation(project(":common-test"))
}

license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    include("**/*.kt")
    include("**/*.java")
    include("**/*.groovy")
}
