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
val springBootVersion = "2.7.18"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux:$springBootVersion") {
        // We should have spring-boot-starter-reactor-netty module due to WebClient bean
        // https://docs.spring.io/spring-boot/docs/2.1.17.RELEASE/reference/html/howto-embedded-web-servers.html
        //      exclude(group = "org.springframework.boot", module = "spring-boot-starter-reactor-netty")
    }
    implementation("org.springframework.boot:spring-boot-starter-undertow:$springBootVersion")
    implementation("io.github.microutils:kotlin-logging-jvm:$microutilsLoggingVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testImplementation(kotlin("test-junit"))
    testImplementation("io.projectreactor:reactor-test:3.4.10")
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
