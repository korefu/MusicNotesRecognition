plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
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
            implementation(libs.kotlinInject.runtime)
            implementation("be.tarsos.dsp:core:2.5")
        }
    }
}

android {
    namespace = "ru.spbu.apmath.nalisin.common_utils"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
