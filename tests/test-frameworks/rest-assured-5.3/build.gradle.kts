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
val drillAutotestAgentVersion: String by parent!!.extra
val junitVersion: String = "5.10.1"

dependencies {
    testImplementation(project(":common-test"))
    testImplementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")

    testImplementation("io.rest-assured:rest-assured:5.3.2")
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
    apiUrl =
        "http://" + rootProject.extra["testsAdminStubServerHost"] as String + ":" + rootProject.extra["testsAdminStubServerPort"] as Int + "/api"
    groupId = "drill-tests"
    enableTestAgent {
        version = drillAutotestAgentVersion
    }
}

license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    include("**/*.kt")
    include("**/*.java")
    include("**/*.groovy")
}

