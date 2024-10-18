import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm").apply(false)
    id("com.epam.drill.integration.cicd")
    id("test-report-aggregation")
}

version = "0.0.1"
group = "com.epam.drill.compatibility"

subprojects {
    val excludedModules = listOf("common-test", "stub-server")
    val appAgentTestModules = listOf("web-servers", "web-frameworks", "http-clients", "async")
    val testAgentTestModules = listOf("test-frameworks")

    val projectName = name
    if (projectName in excludedModules) return@subprojects

    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = JavaVersion.current().toString()
        }
        withType<Test> {
            val host = rootProject.extra["testsAdminStubServerHost"] as String
            val port = rootProject.extra["testsAdminStubServerPort"] as Int
            environment("host" to host)
            environment("port" to port)
            environment("DRILL_API_URL" to "http://$host:$port/api")
            environment("DRILL_IS_COMPATIBILITY_TESTS" to true)
            environment("DRILL_USE_PROTOBUF_SERIALIZER" to false)
            environment("DRILL_USE_GZIP_COMPRESSION" to false)
            environment("DRILL_SCAN_CLASS_DELAY" to "1000")
            environment("DRILL_INSTANCE_ID" to projectName)
            environment("DRILL_SESSION_ID" to projectName)
            dependsOn(":stub-server:serverStart")

            ignoreFailures = true
            testLogging {
                events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
                exceptionFormat = TestExceptionFormat.SHORT
            }
//            dependsOn(":testAggregateTestReport")
        }
    }

    if (parent?.name in appAgentTestModules) {
        apply(plugin = "com.epam.drill.integration.cicd")
        val drillAppAgentVersion: String by extra
        drill {
            groupId = "drill-compatibility-tests"
            appId = project.name.replace(".", "_")
            buildVersion = project.version.toString()
            packagePrefixes = arrayOf("com/epam/test/drill/compatibility")
            enableAppAgent {
                version = drillAppAgentVersion
            }
        }
    }
    if (parent?.name in testAgentTestModules) {
        apply(plugin = "com.epam.drill.integration.cicd")
        val drillTestAgentVersion: String by extra
        drill {
            groupId = "drill-compatibility-tests"
            enableTestAgent {
                version = drillTestAgentVersion
            }
        }
    }
}

allprojects {
    afterEvaluate {
        if (project.path.matches(Regex("^:tests:[^:]+:[^:]+$"))) {
            rootProject.dependencies {
                println("Adding test report aggregation for ${project.path}")
                testReportAggregation(project(project.path))
            }
        }
    }
}

reporting {
    reports {
        val testAggregateTestReport by creating(AggregateTestReport::class) {
            testType.set(TestSuiteType.UNIT_TEST)

        }
    }
}