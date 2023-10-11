plugins {
    id("kmp-library-convention")
    id("publication-convention")
}

version = libs.versions.ktprotoVersion.get()

dependencies {
    commonMainImplementation(projects.libs.crypto)
    commonMainImplementation(libs.koTL.serialization)
    commonMainImplementation(libs.kotlinxSerialization)
    commonMainApi(projects.session)
    commonMainImplementation(libs.kotlinxCoroutines)
    commonMainImplementation(projects.libs.stdlibExtensions)
    commonMainImplementation(projects.libs.io)
    commonMainImplementation(projects.types)
}
