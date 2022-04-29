@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.tasks.factory.dependsOn
import java.util.Properties

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
}

group = "com.meowbox"
version = "1.0"
val mvnArtifactId = name

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jitpack.io")
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
    android()
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
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val androidMain by getting
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
                implementation("androidx.test:core:1.4.0")

                implementation("androidx.test:runner:1.4.0")
                implementation("androidx.test:rules:1.4.0")

                implementation("org.robolectric:robolectric:4.7.3")
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
    destinationDir = File("${project.projectDir}/src/desktopMain/resources")
}

val copyCitiesAndroid = tasks.register<Copy>("copyCitiesAndroid") {
    with(resourceCopySpec)
    destinationDir = File("${project.projectDir}/src/androidMain/res/raw")
}

tasks.named("compileKotlinMetadata").dependsOn(copyCitiesAndroid, copyCitiesDesktop)


/**
 * Publishing-related stuff
 */


val SetupProjectPackageRepo: MavenArtifactRepository.() -> Unit = {
    name = "GitHubPackages"
    url = uri("https://maven.pkg.github.com/tylergannon/kotlin-city-search")
    credentials {
        val props = Properties()
        props.load(rootProject.file("local.properties").bufferedReader())

        fun envGet(name: String) = System.getenv(name)!!
        username = (props["gpr.user"] ?: envGet("USERNAME")).toString()
        password = (props["gpr.key"] ?: envGet("TOKEN")).toString()
    }
}

publishing {
    repositories {
        maven(SetupProjectPackageRepo)
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
            groupId = "com.meowbox.fourpillars"
            artifactId = makeArtifactId(name)
            version
        }
    }
}
