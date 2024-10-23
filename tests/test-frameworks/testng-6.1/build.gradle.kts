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

dependencies {
    testImplementation(project(":common-test"))

    testImplementation("org.testng:testng:6.1.1")
}

tasks {
    test {
        useTestNG()
    }
}

license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    include("**/*.kt")
    include("**/*.java")
    include("**/*.groovy")
}
