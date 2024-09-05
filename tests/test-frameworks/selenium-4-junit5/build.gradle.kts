import java.net.URI
import com.hierynomus.gradle.license.tasks.LicenseCheck
import com.hierynomus.gradle.license.tasks.LicenseFormat

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
val drillAutotestAgentVersion: String by parent!!.extra
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

        environment("host" to rootProject.extra["testsAdminStubServerHost"])
        environment("port" to rootProject.extra["testsAdminStubServerPort"])
        dependsOn(":stub-server:serverStart")
    }
}

drill {
    drillApiUrl =
        "http://" + rootProject.extra["testsAdminStubServerHost"] as String + ":" + rootProject.extra["testsAdminStubServerPort"] as Int + "/api"
    groupId = "drill-tests"
    enableTestAgent {
        version = drillAutotestAgentVersion
        additionalParams = mapOf(
            "devToolsProxyAddress" to "http://localhost:8093",
            "withJsCoverage" to "false",
            "devtoolsAddressReplaceLocalhost" to "host.testcontainers.internal"
        )
    }
}

@Suppress("UNUSED_VARIABLE")
license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    val licenseFormatSources by tasks.registering(LicenseFormat::class) {
        source = fileTree("$projectDir/src").also {
            include("**/*.kt", "**/*.java", "**/*.groovy")
            exclude("**/kni", "**/commonGenerated")
        }
    }
    val licenseCheckSources by tasks.registering(LicenseCheck::class) {
        source = fileTree("$projectDir/src").also {
            include("**/*.kt", "**/*.java", "**/*.groovy")
            exclude("**/kni", "**/commonGenerated")
        }
    }
}
