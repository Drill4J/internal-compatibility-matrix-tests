import com.hierynomus.gradle.license.tasks.LicenseCheck
import com.hierynomus.gradle.license.tasks.LicenseFormat
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.presetName
import java.net.URI

plugins {
    kotlin("multiplatform")
    id("com.github.johnrengelman.shadow")
    id("com.github.hierynomus.license")
}
group = "com.epam.drill.compatibility"
version = rootProject.version
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.current().toString()
}

repositories {
    mavenLocal()
    mavenCentral()
}

val nativeAgentLibName: String by parent!!.extra
val macosLd64: String by parent!!.extra

kotlin {
    val configureNativeTarget: KotlinNativeTarget.() -> Unit = {
        binaries.sharedLib(nativeAgentLibName, setOf(DEBUG))
    }
    targets {
        jvm()
        linuxX64(configure = configureNativeTarget)
        macosX64(configure = configureNativeTarget).apply {
            if (macosLd64.toBoolean()) {
                binaries.all {
                    linkerOpts("-ld64")
                }
            }
        }
        mingwX64(configure = configureNativeTarget).apply {
            binaries.all {
                linkerOpts("-lpsapi", "-lwsock32", "-lws2_32", "-lmswsock")
            }
        }
    }
    sourceSets {
        targets.withType<KotlinNativeTarget>()[HostManager.host.presetName].compilations.forEach {
            it.defaultSourceSet.kotlin.srcDir("src/native${it.compilationName.capitalize()}/kotlin")
            it.defaultSourceSet.resources.srcDir("src/native${it.compilationName.capitalize()}/resources")
        }
        val commonMain by getting {
            dependencies {
                implementation(project(":agent-instrumentation"))
                implementation(project(":common"))
            }
        }
        val configureNativeMainDependencies: KotlinSourceSet.() -> Unit = {
            dependsOn(commonMain)
            dependencies {
                implementation(project(":knasm"))
            }
        }
        val mingwX64Main by getting(configuration = configureNativeMainDependencies)
        val linuxX64Main by getting(configuration = configureNativeMainDependencies)
        val macosX64Main by getting(configuration = configureNativeMainDependencies)
    }
    tasks {
        val filterOutCurrentPlatform: (KotlinNativeTarget) -> Boolean = {
            it.targetName != HostManager.host.presetName
        }
        val copyNativeClassesTask: (KotlinCompilation<*>) -> Unit = {
            val taskName = "copyNativeClasses${it.target.targetName.capitalize()}${it.compilationName.capitalize()}"
            val copyNativeClasses: TaskProvider<Copy> = register(taskName, Copy::class) {
                group = "build"
                from("src/native${it.compilationName.capitalize()}/kotlin")
                into("src/${it.target.targetName}${it.compilationName.capitalize()}/kotlin/gen")
            }
            it.compileKotlinTask.dependsOn(copyNativeClasses.get())
        }
        val cleanNativeClassesTask: (KotlinCompilation<*>) -> Unit = {
            val taskName = "cleanNativeClasses${it.target.targetName.capitalize()}${it.compilationName.capitalize()}"
            val cleanNativeClasses: TaskProvider<Delete> = register(taskName, Delete::class) {
                group = "build"
                delete("src/${it.target.targetName}${it.compilationName.capitalize()}/kotlin/gen")
            }
            clean.get().dependsOn(cleanNativeClasses.get())
        }
        targets.withType<KotlinNativeTarget>().filter(filterOutCurrentPlatform)
            .flatMap(KotlinNativeTarget::compilations)
            .onEach(copyNativeClassesTask)
            .onEach(cleanNativeClassesTask)

        val jvmMainCompilation = targets.withType<KotlinJvmTarget>()["jvm"].compilations["main"]
        val runtimeJar by registering(ShadowJar::class) {
            mergeServiceFiles()
            isZip64 = true
            archiveFileName.set("drill-runtime.jar")
            from(jvmMainCompilation.runtimeDependencyFiles, jvmMainCompilation.output)
            dependencies {
                exclude("/META-INF/services/javax.servlet.ServletContainerInitializer")
                exclude("/ch/qos/logback/classic/servlet/*")
            }
        }
        register("buildAgentJar") {
            val kotlinTargets = targets.withType<KotlinNativeTarget>().getByName(HostManager.host.presetName)
                .binaries.getSharedLib(nativeAgentLibName, NativeBuildType.DEBUG)
            dependsOn(runtimeJar)
            dependsOn(kotlinTargets.linkTask)
            doLast {
                copy {
                    from(runtimeJar.get().outputs.files.singleFile.path)
                    into("${project.rootDir}/drill-agent")
                }
                copy {
                    from(kotlinTargets.outputFile.path)
                    into("${project.rootDir}/drill-agent")
                }
            }
        }
    }
}

@Suppress("UNUSED_VARIABLE")
license {
    headerURI = URI("https://raw.githubusercontent.com/Drill4J/drill4j/develop/COPYRIGHT")
    val licenseFormatSources by tasks.registering(LicenseFormat::class) {
        source = fileTree("$projectDir/src").also {
            include("**/*.kt", "**/*.java", "**/*.groovy")
            exclude("**/kni")
        }
    }
    val licenseCheckSources by tasks.registering(LicenseCheck::class) {
        source = fileTree("$projectDir/src").also {
            include("**/*.kt", "**/*.java", "**/*.groovy")
            exclude("**/kni")
        }
    }
}