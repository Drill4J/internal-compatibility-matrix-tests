import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.presetName
import java.net.URI

plugins {
    kotlin("jvm")
    id("com.github.hierynomus.license")
}

version = rootProject.version
group = rootProject.group

repositories {
    mavenCentral()
}

val nativeAgentLibName: String by parent!!.extra
val microutilsLoggingVersion: String by parent!!.extra
val serverVersion = "10.0.20"

dependencies {
    implementation("io.github.microutils:kotlin-logging-jvm:$microutilsLoggingVersion")
    implementation("org.eclipse.jetty:jetty-server:$serverVersion")
    testImplementation(kotlin("test-junit"))
    testImplementation(project(":common-test"))
    evaluationDependsOn(":test-agent")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.current().toString()
    }
    test {
        val pathToBinary: String
        val pathToRuntimeJar: String
        val hostPresetName = HostManager.host.presetName
        val targetJarName = "drill-runtime.jar"
        val fileName = when {
            HostManager.hostIsMingw -> "drill_agent.dll"
            HostManager.hostIsMac -> "libdrill_agent.dylib"
            else -> "libdrill_agent.so"
        }
        val property = providers.systemProperty("test-agent.binaries")
        if (property.isPresent) {
            val binariesPath = property.get()
            pathToBinary = "$binariesPath/$hostPresetName/drill-agentDebugShared/$fileName"
            pathToRuntimeJar = "$binariesPath/$targetJarName"
        } else {
            val buildDir = project(":test-agent").buildDir.path
            pathToRuntimeJar = "$buildDir/libs/$targetJarName"
            pathToBinary = "$buildDir/bin/$hostPresetName/drill-agentDebugShared/$fileName"
        }
        jvmArgs = listOf(
            "-agentpath:$pathToBinary=$pathToRuntimeJar"
        )
    }
}

license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    include("**/*.kt")
    include("**/*.java")
    include("**/*.groovy")
}
