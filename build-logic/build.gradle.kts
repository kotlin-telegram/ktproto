
plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    api(libs.kotlinPlugin)
    api(libs.kotlinxSerializationPlugin)
}
