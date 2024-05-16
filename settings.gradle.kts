rootProject.name = "MusicNotesRecognition"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven {
            name = "TarsosDSP repository"
            url = uri("https://mvn.0110.be/releases")
        }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
        maven { url = uri("https://github.com/kshoji/JFugue-for-Android/raw/master/jfugue-android/snapshot") }
        maven { url = uri("https://github.com/kshoji/USB-MIDI-Driver/raw/master/MIDIDriver/snapshots") }
    }
}

include(":app")
include(":common-entities")
include(":frequency-recognition-api")
include(":audio-splitter")
include(":frequency-recognition-fft")
include(":common-utils")
include(":audio-to-notation-converter")
include(":musicxml-writer")
include("loudness-analyzer")
