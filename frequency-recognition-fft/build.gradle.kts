plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.ksp)
}

kotlin {
    jvm()
    jvmToolchain(11)

    sourceSets {
        commonMain.dependencies {
            api(project(":frequency-recognition-api"))

            implementation(project(":common-entities"))
            implementation(project(":common-utils"))
            implementation("be.tarsos.dsp:core:2.5")
            implementation(libs.kotlinInject.runtime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

dependencies {
    kspCommonMainMetadata(libs.kotlinInject.compiler)
}
