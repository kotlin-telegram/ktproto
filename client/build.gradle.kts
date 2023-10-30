plugins {
    id("kmp-library-convention")
    id("publication-convention")
}

version = libs.versions.ktprotoVersion.get()

dependencies {
    commonMainApi(projects.session)
    commonMainApi(libs.koTL.serialization)

    commonMainImplementation(projects.libs.crypto)
    commonMainImplementation(libs.kotlinxSerialization)
    commonMainImplementation(libs.kotlinxCoroutines)
    commonMainImplementation(projects.libs.stdlibExtensions)
    commonMainImplementation(projects.libs.io)
    commonMainImplementation(projects.types)
}
