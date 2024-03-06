import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.presetName
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

"com.epam.drill.compatibility"
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.current().toString()
}

version = rootProject.version
repositories {
    mavenCentral()
}

val microutilsLoggingVersion: String by parent!!.extra

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("io.github.microutils:kotlin-logging-jvm:$microutilsLoggingVersion")
    testImplementation(project(":abstract-test"))
    evaluationDependsOn(":test-agent")
}

val nativeAgentLibName: String by parent!!.extra

tasks {
    val kotlinTargets = (project(":test-agent").extensions.getByName("kotlin") as KotlinMultiplatformExtension)
        .targets.withType<KotlinNativeTarget>().getByName(HostManager.host.presetName)
        .binaries.getSharedLib(nativeAgentLibName, NativeBuildType.DEBUG)
    test {
        jvmArgs = listOf(
            "-agentpath:${kotlinTargets.outputFile.path}=${
                project(":test-agent").tasks.named("runtimeJar").get().outputs.files.singleFile
            }"
        )
        dependsOn(kotlinTargets.linkTask)
        dependsOn(project(":test-agent").tasks["runtimeJar"])
    }
    named<BootJar>("bootJar") {
        enabled = false
    }
}
