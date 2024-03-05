import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.epam.drill.compatibility.matrix"
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

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
    test {
        useJUnitPlatform()
    }
    val testJar by registering(Jar::class) {
        from(sourceSets.test.get().output)
        archiveClassifier.set("test")
    }
    configurations {
        create("testArtifacts") {
            extendsFrom(configurations["testImplementation"])
            outgoing.artifact(testJar.get())
        }
    }
}

artifacts {
    add("testArtifacts", tasks["testJar"])
}
