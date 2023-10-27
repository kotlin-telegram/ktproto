plugins {
    id("kmp-library-convention")
    id("publication-convention")
}

version = libs.versions.ktprotoVersion.get()

dependencies {
    commonMainApi(projects.session)

    commonMainImplementation(projects.libs.crypto)
    commonMainImplementation(libs.koTL.serialization)
    commonMainImplementation(libs.kotlinxSerialization)
    commonMainImplementation(libs.kotlinxCoroutines)
    commonMainImplementation(projects.libs.stdlibExtensions)
    commonMainImplementation(projects.libs.io)
    commonMainImplementation(projects.types)
}
