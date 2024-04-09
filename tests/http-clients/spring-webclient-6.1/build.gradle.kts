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
val springBootVersion = "2.7.18"

dependencies {
    implementation("io.github.microutils:kotlin-logging-jvm:$microutilsLoggingVersion")
    implementation("org.springframework:spring-webflux:6.1.5")
    implementation("org.springframework:spring-context:6.1.5")
    implementation("io.projectreactor.netty:reactor-netty:1.1.17")
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
