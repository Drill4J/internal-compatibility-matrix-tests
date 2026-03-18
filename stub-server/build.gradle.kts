import java.net.URI
import com.hierynomus.gradle.license.tasks.LicenseCheck
import com.hierynomus.gradle.license.tasks.LicenseFormat
import com.github.psxpaul.task.JavaExecFork
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    kotlin("jvm")
    id("com.github.hierynomus.license")
    id("com.github.psxpaul.execfork")
}

val javaVersion = (rootProject.findProperty("javaVersion") as String?)?.toInt() ?: 17

kotlin {
    jvmToolchain(javaVersion)
}

group = rootProject.group
version = rootProject.version

val microutilsLoggingVersion: String by rootProject.extra
val logbackVersion: String by parent!!.extra

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":common-test"))
    implementation("io.github.microutils:kotlin-logging-jvm:$microutilsLoggingVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
}

@Suppress("UNUSED_VARIABLE")
tasks {
    val stubServerHost = rootProject.extra["testsAdminStubServerHost"] as String
    val stubServerPort = rootProject.extra["testsAdminStubServerPort"] as Int
    val serverStart by creating(JavaExecFork::class) {
        group = "verification"
        workingDir = jar.get().archiveFile.get().asFile.parentFile
        classpath = sourceSets.main.get().runtimeClasspath
        main = "MainKt"
        jvmArgs = mutableListOf("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5015")
        args = mutableListOf(stubServerHost, stubServerPort.toString())
        waitForPort = stubServerPort
        killDescendants = false
        // Use the same Java toolchain version for running as for compiling
        executable = project.extensions.getByType<JavaToolchainService>()
            .launcherFor { languageVersion.set(JavaLanguageVersion.of(javaVersion)) }
            .get().executablePath.asFile.absolutePath
    }
    serverStart.dependsOn(jar)
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
