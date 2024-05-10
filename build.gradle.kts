import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.presetName

plugins {
    kotlin("jvm").apply(false)
    kotlin("multiplatform").apply(false)
    kotlin("plugin.serialization").apply(false)
    id("com.github.johnrengelman.shadow").apply(false)
    id("com.github.hierynomus.license").apply(false)
}

version = "0.0.1"
group = "com.epam.drill.compatibility"

repositories {
    mavenLocal()
    mavenCentral()
}

subprojects {
    if (!projectDir.invariantSeparatorsPath.removePrefix(rootDir.invariantSeparatorsPath).startsWith("/tests/"))
        return@subprojects

    tasks {
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = JavaVersion.current().toString()
        }
        withType<Test> {
            val pathToBinary: String
            val pathToRuntimeJar: String
            val hostPresetName = HostManager.host.presetName
            val runtimeJarFileName = "drill-runtime.jar"
            val binaryFileName = when {
                HostManager.hostIsMingw -> "drill_agent.dll"
                HostManager.hostIsMac -> "libdrill_agent.dylib"
                else -> "libdrill_agent.so"
            }

            // property "test-agent.binaries" is required to specify the path where artifacts
            // are uploaded during a build in GitHub Actions.
            val property = providers.systemProperty("test-agent.binaries")
            if (property.isPresent) {
                //GitHub action build
                val binariesPath = property.get()
                pathToBinary = "$binariesPath/$hostPresetName/drill-agentDebugShared/$binaryFileName"
                pathToRuntimeJar = "$binariesPath/$runtimeJarFileName"
            } else {
                //Local build
                val buildDir = project(":test-agent").buildDir.path
                pathToBinary = "$buildDir/bin/$hostPresetName/drill-agentDebugShared/$binaryFileName"
                pathToRuntimeJar = "$buildDir/libs/$runtimeJarFileName"
            }
            jvmArgs = listOf(
                "-agentpath:$pathToBinary=$pathToRuntimeJar"
            )
        }
    }

}
