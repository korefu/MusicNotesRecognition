plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    jvm("desktop")
    jvmToolchain(11)

    sourceSets {
        commonMain.dependencies {
            implementation(project(":common-entities"))
            implementation(project(":common-utils"))
            implementation(project(":audio-splitter"))
            implementation(project(":frequency-recognition-fft"))
            implementation(project(":musicxml-writer"))
            implementation(libs.kotlinInject.runtime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        val desktopMain by getting
        desktopMain.dependencies {
            implementation("com.github.JensPiegsa:jfugue:5.0.9")
        }
        androidMain.dependencies {
            implementation("jp.kshoji:jfugue-android:4.0.3:@aar")
            implementation("jp.kshoji:midi-driver:0.1.1:@aar")
        }
    }
}

android {
    namespace = "ru.spbu.apmath.nalisin.audio_to_midi_converter"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    kspCommonMainMetadata(libs.kotlinInject.compiler)
}
