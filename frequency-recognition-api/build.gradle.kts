plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()
    jvmToolchain(11)
}

dependencies {
    commonMainImplementation(project(":common-entities"))
}