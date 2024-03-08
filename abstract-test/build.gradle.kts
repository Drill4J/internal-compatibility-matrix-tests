import com.hierynomus.gradle.license.tasks.LicenseCheck
import com.hierynomus.gradle.license.tasks.LicenseFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

group = "com.epam.drill.compatibility"
version = "1.0.0"

plugins {
    kotlin("jvm")
    id("com.github.hierynomus.license")
}

val microutilsLoggingVersion: String by parent!!.extra

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux:2.7.18")
    implementation("org.springframework.boot:spring-boot-starter-test:2.7.18")
    implementation(kotlin("test-junit"))
    implementation("io.github.microutils:kotlin-logging-jvm:$microutilsLoggingVersion")
    implementation(project(":common"))
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.current().toString()
    }
    test {
        useJUnitPlatform()
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