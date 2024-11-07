import java.net.URI

plugins {
    kotlin("jvm")
    id("com.github.hierynomus.license")
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

val junitVersion: String = "5.10.1"
val cucumberVersion: String = "5.7.0"

dependencies {
    testImplementation(project(":common-test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.platform:junit-platform-suite:1.10.0")
    testImplementation("io.cucumber:cucumber-java:$cucumberVersion")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:$cucumberVersion")
}

tasks {
    test {
        useJUnitPlatform()
    }
}

license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    include("**/*.kt")
    include("**/*.java")
    include("**/*.groovy")
}
