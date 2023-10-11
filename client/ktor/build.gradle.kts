plugins {
    id("kmp-library-convention")
    id("publication-convention")
}

version = libs.versions.ktprotoVersion.get()

dependencies {
    commonMainApi(projects.client)
    commonMainImplementation(projects.libs.io)
    commonMainImplementation(projects.libs.stdlibExtensions)
    commonMainImplementation(projects.types)
    commonMainImplementation(libs.koTL.serialization)
    commonMainApi(libs.ktor.client)
    commonMainApi(libs.kotlinxSerialization)
    jvmMainImplementation(libs.ktor.client.cio)
    jvmMainImplementation(libs.ktor.client.logging)
}
