plugins {
    id("kmp-library-convention")
    id("publication-convention")
}

version = libs.versions.ktprotoVersion.get()

dependencies {
    commonMainApi(projects.transport)
    commonMainImplementation(libs.kotlinxCoroutines)
    commonMainImplementation(projects.types)
    commonMainImplementation(projects.libs.stdlibExtensions)
    commonMainImplementation(projects.libs.io)
}
