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
val serverVersion = "10.1.19"

dependencies {
    implementation("io.github.microutils:kotlin-logging-jvm:$microutilsLoggingVersion")
    implementation("org.apache.tomcat.embed:tomcat-embed-core:$serverVersion")
    testImplementation(kotlin("test-junit"))
    testImplementation(project(":common-test"))
    evaluationDependsOn(":test-agent")
}

license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    include("**/*.kt")
    include("**/*.java")
    include("**/*.groovy")
}
