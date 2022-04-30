@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import java.util.Properties


buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.20")
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.8.2.0")
    }
}


plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
}

group = "com.meowbox"
version = "1.0.10"
val mvnArtifactId = name

repositories {
    google()
    mavenCentral()
//    privateMvnRepo("maven-repo")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jitpack.io")
}

afterEvaluate {

}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(IR) {
        browser {
            testTask {
                useKarma {
                    useFirefoxHeadless()
                }
            }
        }
    }
    android {
        publishLibraryVariants = listOf("release", "debug")
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1")
                implementation("io.kotest:kotest-assertions-core:5.2.3")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm("axios", "0.26.1"))
                implementation("org.jetbrains.kotlin-wrappers:kotlin-extensions:1.0.1-pre.332-kotlin-1.6.21")
            }
        }
        val jsTest by getting

        val jvmMain by getting
        val jvmTest by getting
        val androidMain by getting
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
                implementation("androidx.test:core:1.4.0")

                implementation("androidx.test:runner:1.4.0")
                implementation("androidx.test:rules:1.4.0")
                implementation("org.robolectric:robolectric:4.6.1")
            }
        }
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 26
        targetSdk = 31
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    lint {
        this.isAbortOnError = false
        this.isCheckTestSources = false
        this.isCheckReleaseBuilds = false
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

val resourceCopySpec = copySpec {
    from("${project.projectDir}/src/commonMain/resources/cities.tsv.gz")
    rename { "citiesgz" }
}



val copyCitiesDesktop = tasks.register<Copy>("copyCitiesDesktop") {
    with(resourceCopySpec)
    destinationDir = File("${project.projectDir}/src/jvmMain/resources")
}

val copyCitiesAndroid = tasks.register<Copy>("copyCitiesAndroid") {
    with(resourceCopySpec)
    destinationDir = File("${project.projectDir}/src/androidMain/res/raw")
}

afterEvaluate {
    tasks.named("jvmMainClasses").dependsOn(copyCitiesDesktop)
    tasks.named("preBuild").dependsOn(copyCitiesAndroid, copyCitiesDesktop)


    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
        }
    }
}


/**
 * Publishing-related stuff
 */



val privateMvnRepo: RepositoryHandler.(String) -> Unit = { repoName ->
    maven {
        this.name = "GitHubPackages"
        this.url = uri("https://maven.pkg.github.com/tylergannon/$repoName")
        credentials {
            Properties().also { props ->
                props.load(rootProject.file("local.properties").bufferedReader())
                username = props["gpr.user"].toString()
                password = props["gpr.key"].toString()
            }
        }
    }
}

publishing {
    repositories {
        privateMvnRepo("kotlin-city-search")
    }
}


afterEvaluate {
    configure<PublishingExtension> {
        publications.all {
            val mavenPublication = this as? MavenPublication
            mavenPublication?.artifactId = makeArtifactId(name)
        }
    }
}

configure<PublishingExtension> {
    publications {
        withType<MavenPublication> {
            groupId = "com.meowbox.citysearch"
            artifactId = makeArtifactId(name)
            version
        }
    }
}

fun String.dasherize() = fold("") {acc, value ->
    if (value.isUpperCase()) {
        "$acc-${value.toLowerCase()}"
    } else {
        "$acc$value"
    }
}

fun makeArtifactId(name: String) =
    if ("kotlinMultiplatform" in name) {
        mvnArtifactId
    } else {
        "$mvnArtifactId-${name.dasherize()}"
    }

