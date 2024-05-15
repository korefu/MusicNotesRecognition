plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.ksp)
}

kotlin {
    jvm()
    jvmToolchain(11)

    sourceSets {
        commonMain.dependencies {
            implementation(project(":common-entities"))
            implementation(project(":common-utils"))
            implementation(libs.kotlinInject.runtime)
        }
    }
}

dependencies {
    kspCommonMainMetadata(libs.kotlinInject.compiler)
}