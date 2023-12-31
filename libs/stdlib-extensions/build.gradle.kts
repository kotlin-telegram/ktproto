plugins {
    id("kmp-library-convention")
    id("publication-convention")
}

version = libs.versions.ktprotoVersion.get()

kotlin {
    js {
        nodejs()
        binaries.executable()
    }
}

dependencies {
    commonMainImplementation(libs.kotlinxCoroutines)
    commonMainImplementation(libs.koTL.serialization)
    commonTestImplementation(kotlin("test"))
}
