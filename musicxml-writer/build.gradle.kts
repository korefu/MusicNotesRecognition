plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()
    jvmToolchain(11)
    sourceSets {
        commonMain.dependencies {
            implementation(project(":common-entities"))
            implementation("org.redundent:kotlin-xml-builder:1.9.1")
            implementation(libs.kotlinInject.runtime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
