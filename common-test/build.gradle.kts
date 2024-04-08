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
    compileOnly("org.springframework:spring-test:5.3.31")
    compileOnly("org.springframework:spring-beans:5.3.31")
    compileOnly("org.springframework:spring-web:5.3.31")
    compileOnly("org.springframework:spring-context:5.3.31")
    compileOnly("org.springframework.boot:spring-boot-starter-test:2.7.18")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure:2.7.18")
    compileOnly("io.projectreactor:reactor-core:3.4.34")
    compileOnly("org.simpleframework:simple-http:6.0.1")

    compileOnly(kotlin("test-junit"))
    compileOnly(project(":common"))
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
