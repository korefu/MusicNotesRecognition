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
            implementation("org.redundent:kotlin-xml-builder:1.9.1")
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
