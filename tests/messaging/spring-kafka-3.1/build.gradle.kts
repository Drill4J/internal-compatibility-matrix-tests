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

dependencies {
    implementation("org.springframework.kafka:spring-kafka:3.1.4")
    testImplementation(kotlin("test-junit"))
    testImplementation(project(":common-test"))
    testImplementation("org.testcontainers:kafka:1.19.8")
}

license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    include("**/*.kt")
    include("**/*.java")
    include("**/*.groovy")
}
