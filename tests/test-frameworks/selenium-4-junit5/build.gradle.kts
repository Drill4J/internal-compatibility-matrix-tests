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

dependencies {
    testImplementation(project(":common-test"))
    testImplementation("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")

    testImplementation("org.testcontainers:junit-jupiter:1.15.2")

    testImplementation("org.testcontainers:testcontainers:1.19.0")
    testImplementation("org.testcontainers:selenium:1.19.0")
    testImplementation("org.testcontainers:junit-jupiter:1.19.0")
    testImplementation("org.testcontainers:mockserver:1.19.0")

    testImplementation("org.seleniumhq.selenium:selenium-java:4.14.0")

    testImplementation("org.mock-server:mockserver-netty:5.15.0")
    testImplementation("org.mock-server:mockserver-client-java:5.15.0")
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
    drillApiUrl = "http://" + rootProject.extra["testsAdminStubServerHost"] as String + ":" + rootProject.extra["testsAdminStubServerPort"] as Int + "/api"
    groupId = "drill-tests"
    enableTestAgent {
        enabled = false
        version = drillAutotestAgentVersion
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
