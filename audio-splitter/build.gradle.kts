plugins {
    alias(libs.plugins.kotlinMultiplatform)
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
