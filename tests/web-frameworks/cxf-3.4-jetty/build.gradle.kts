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
val apacheCxfVersion = "3.4.10"

dependencies {
    implementation("io.github.microutils:kotlin-logging-jvm:$microutilsLoggingVersion")
    implementation("org.apache.cxf:cxf-rt-frontend-jaxrs:$apacheCxfVersion")
    implementation("org.apache.cxf:cxf-rt-transports-http:$apacheCxfVersion")
    implementation("org.apache.cxf:cxf-rt-rs-service-description:$apacheCxfVersion")
    testImplementation("org.apache.cxf:cxf-rt-transports-http:$apacheCxfVersion")
    testImplementation("org.apache.cxf:cxf-rt-transports-http-jetty:$apacheCxfVersion")

    testImplementation(kotlin("test-junit"))
    testImplementation(project(":common-test"))
}

license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    include("**/*.kt")
    include("**/*.java")
    include("**/*.groovy")
}
