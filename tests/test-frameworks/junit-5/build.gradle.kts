import java.net.URI

plugins {
    kotlin("jvm")
    id("com.github.hierynomus.license")
    id("com.epam.drill.integration.cicd")
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

val drillAutotestAgentVersion: String by parent!!.extra

tasks {
    test {
        useJUnitPlatform()

        systemProperties("sessionId" to "junit-5")
        environment("host" to rootProject.extra["testsAdminStubServerHost"])
        environment("port" to rootProject.extra["testsAdminStubServerPort"])
        dependsOn(":stub-server:serverStart")
    }
}

drill {
    apiUrl = "http://" + rootProject.extra["testsAdminStubServerHost"] as String + ":" + rootProject.extra["testsAdminStubServerPort"] as Int + "/api"
    groupId = "drill-tests"
    enableTestAgent {
        version = drillAutotestAgentVersion
        additionalParams = mapOf("sessionId" to "junit-5")
    }
}

license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    include("**/*.kt")
    include("**/*.java")
    include("**/*.groovy")
}
