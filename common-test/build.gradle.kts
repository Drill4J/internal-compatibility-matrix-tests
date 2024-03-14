import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    kotlin("jvm")
    id("com.github.hierynomus.license")
}

group = rootProject.group
version = rootProject.version

val microutilsLoggingVersion: String by parent!!.extra

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("io.github.microutils:kotlin-logging-jvm:$microutilsLoggingVersion")
    implementation("org.springframework.boot:spring-boot-starter-webflux:2.7.18")
    implementation("org.springframework.boot:spring-boot-starter-test:2.7.18")
    implementation(kotlin("test-junit"))
    implementation(project(":common"))
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.current().toString()
    }
}

license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    include("**/*.kt")
    include("**/*.java")
    include("**/*.groovy")
}
