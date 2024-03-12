import com.hierynomus.gradle.license.tasks.LicenseCheck
import com.hierynomus.gradle.license.tasks.LicenseFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.target.HostManager
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.net.URI

plugins {
    kotlin("jvm")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.github.hierynomus.license")
}

group = "com.epam.drill.compatibility"
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.current().toString()
}

version = rootProject.version

repositories {
    mavenCentral()
}

val microutilsLoggingVersion: String by parent!!.extra

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-undertow")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("io.github.microutils:kotlin-logging-jvm:$microutilsLoggingVersion")
    testImplementation(project(":abstract-test"))
    configurations {
        all {
            exclude(group = "ch.qos.logback", module = "logback-classic")
        }
    }
}


val nativeAgentLibName: String by parent!!.extra

tasks {
    test {
        val fileName = when {
            HostManager.hostIsMingw -> "drill_agent.dll"
            HostManager.hostIsMac -> "libdrill_agent.dylib"
            else -> "libdrill_agent.so"
        }
        jvmArgs = listOf(
            "-agentpath:${rootProject.projectDir.path}/drill-agent/$fileName=${rootProject.projectDir.path}/drill-agent/drill-runtime.jar"
        )
    }
    named<BootJar>("bootJar") {
        enabled = false
    }
    licenseTest {
        enabled = false
    }
}

@Suppress("UNUSED_VARIABLE")
license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    val licenseFormatSources by tasks.registering(LicenseFormat::class) {
        source = fileTree("$projectDir/src").also {
            include("**/*.kt", "**/*.java", "**/*.groovy")
            exclude("**/commonGenerated")
        }
    }
    val licenseCheckSources by tasks.registering(LicenseCheck::class) {
        source = fileTree("$projectDir/src").also {
            include("**/*.kt", "**/*.java", "**/*.groovy")
            exclude("**/commonGenerated")
        }
    }
}