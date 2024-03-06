import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

group = "com.epam.drill.compatibility"
version = "1.0.0"

plugins {
    kotlin("jvm")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

val microutilsLoggingVersion: String by parent!!.extra

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-webflux:2.7.18")
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
    named<BootJar>("bootJar") {
        enabled = false
    }
}
