plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()
    jvmToolchain(11)
}

dependencies {
    commonMainImplementation(project(":common-entities"))
    commonMainImplementation(project(":common-utils"))
    commonMainImplementation("be.tarsos.dsp:core:2.5")
    commonMainImplementation(libs.kotlinInject.runtime)
}