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

dependencies {
    testImplementation(project(":common-test"))

    testImplementation("org.testng:testng:6.1.1")
}

val drillAutotestAgentVersion: String by parent!!.extra

tasks {
    //TODO: fix EPMDJ-10905
//    test {
//        useTestNG()
//
//        ignoreFailures = true
//
//        environment("host" to rootProject.extra["testsAdminStubServerHost"])
//        environment("port" to rootProject.extra["testsAdminStubServerPort"])
//        dependsOn(":stub-server:serverStart")
//    }
}

drill {
    drillApiUrl = "http://" + rootProject.extra["testsAdminStubServerHost"] as String + ":" + rootProject.extra["testsAdminStubServerPort"] as Int + "/api"
    groupId = "drill-tests"
    enableTestAgent {
        version = drillAutotestAgentVersion
        additionalParams = mapOf("sessionId" to "testng-6.1")
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
