import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import java.net.ServerSocket

plugins {
    kotlin("jvm").apply(false)
    id("com.epam.drill.integration.cicd")
}

version = "0.0.1"
group = "com.epam.drill.compatibility"

val jdkVersion = (findProperty("javaVersion") as String?)?.toInt() ?: 17
// Initialize host and port at configuration time so they're available to all subprojects
val (stubServerHost, stubServerPort) = ServerSocket(0).use { "127.0.0.1" to it.localPort }
rootProject.extra["testsAdminStubServerHost"] = stubServerHost
rootProject.extra["testsAdminStubServerPort"] = stubServerPort


subprojects {
    val excludedModules = listOf("common-test", "stub-server")
    val appAgentTestModules = listOf("web-servers", "web-frameworks", "http-clients", "async",
        "websocket-clients", "websocket-clients-frameworks",
        "websocket-servers", "websocket-servers-frameworks",
        "websocket-messages", "websocket-messages-frameworks",
        "messaging")
    val testAgentTestModules = listOf("test-frameworks")

    val projectName = name
    if (projectName in excludedModules) return@subprojects

    repositories {
        mavenLocal()
        mavenCentral()
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")

    extensions.configure<KotlinJvmProjectExtension> {
        jvmToolchain(jdkVersion)
    }

    tasks {
        withType<Test> {
            dependsOn(":stub-server:serverStart")
            val host = rootProject.extra["testsAdminStubServerHost"] as String
            val port = rootProject.extra["testsAdminStubServerPort"] as Int
            environment("host" to host)
            environment("port" to port)
            environment("DRILL_API_URL" to "http://$host:$port/api")
            environment("DRILL_USE_PROTOBUF_SERIALIZER" to false)
            environment("DRILL_USE_GZIP_COMPRESSION" to false)
            environment("DRILL_INSTRUMENTATION_COMPATIBILITY_TESTS_ENABLED" to true)
            environment("DRILL_INSTRUMENTATION_WS_ENABLED" to true)
            environment("DRILL_INSTRUMENTATION_TTL_ENABLED" to true)
            environment("DRILL_INSTRUMENTATION_JAVA_HTTP_CLIENT_ENABLED" to true)
            environment("DRILL_INSTRUMENTATION_KAFKA_ENABLED" to true)
            environment("DRILL_SCAN_CLASS_DELAY" to "1000")
            environment("DRILL_INSTANCE_ID" to projectName)
            environment("DRILL_TEST_SESSION_ID" to projectName)

            ignoreFailures = true
            testLogging {
                events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
                exceptionFormat = TestExceptionFormat.SHORT
            }
        }
    }

    apply(plugin = "com.epam.drill.integration.cicd")
    val drillAgentVersion: String by extra
    val drillAgentMode: String by extra
    drill {
        groupId = "drill-compatibility-tests"
        appId = project.name.replace(".", "_")
        buildVersion = project.version.toString()
        packagePrefixes = arrayOf("com/epam/drill/compatibility/apps")
        agent {
            version = drillAgentVersion
            agentMode = drillAgentMode
            logLevel = "INFO;com.epam.drill.agent.instrument=DEBUG;com.epam.drill.agent.configuration=DEBUG"
        }
        if (parent?.name in appAgentTestModules) {
            coverage()
            classScanning {
                runtime = true
            }
        }
        if (parent?.name in testAgentTestModules) {
            testTracing()
        }
    }

}