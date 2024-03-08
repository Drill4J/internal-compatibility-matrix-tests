import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.epam.drill.compatibility"
version = "1.0.0"

plugins {
    kotlin("jvm")
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
