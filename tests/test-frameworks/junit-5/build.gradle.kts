import java.net.URI

plugins {
    kotlin("jvm")
    id("com.github.hierynomus.license")
}

group = rootProject.group
version = rootProject.version

val junitVersion: String = "5.7.2"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(project(":common-test"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
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
