import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.presetName

plugins {
    kotlin("jvm")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.epam.drill.compatibility.matrix"
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-reactor-netty")
    }
    implementation("org.springframework.boot:spring-boot-starter-undertow")

    testImplementation("io.projectreactor:reactor-test")
    testImplementation(project(path = ":abstract-test", configuration = "testArtifacts"))
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
}
