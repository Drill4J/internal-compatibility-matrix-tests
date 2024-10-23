import java.net.URI

plugins {
    kotlin("jvm")
    id("com.github.hierynomus.license")
    id("com.epam.drill.integration.cicd")
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

val logbackVersion: String by parent!!.extra
val junitVersion: String = "5.10.0"
val testcontainersVersion: String = "1.19.8"
val mockserverVersion: String = "5.15.0"
val seleniumVersion: String = "4.24.0"

dependencies {
    testImplementation(project(":common-test"))
    testImplementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.mock-server:mockserver-netty:$mockserverVersion")
    testImplementation("org.mock-server:mockserver-client-java:$mockserverVersion")
    testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
    testImplementation("org.testcontainers:selenium:$testcontainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:mockserver:$testcontainersVersion")

    testImplementation("org.seleniumhq.selenium:selenium-java:$seleniumVersion")
}

tasks {
    test {
        useJUnitPlatform()
    }
}

val drillTestAgentVersion: String by extra
drill {
    groupId = "drill-compatibility-tests"
    enableTestAgent {
        version = drillTestAgentVersion
        additionalParams = mapOf(
            "devToolsProxyAddress" to "http://localhost:8093",
            "withJsCoverage" to "false",
            "devtoolsAddressReplaceLocalhost" to "host.testcontainers.internal"
        )
    }
}

license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    include("**/*.kt")
    include("**/*.java")
    include("**/*.groovy")
}
