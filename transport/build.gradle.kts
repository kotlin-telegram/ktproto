plugins {
    id("kmp-library-convention")
    id("publication-convention")
}

version = libs.versions.ktprotoVersion.get()

dependencies {
    commonMainImplementation(libs.kotlinxCoroutines)
    commonMainImplementation(projects.libs.stdlibExtensions)
    commonMainImplementation(projects.libs.io)
    commonMainImplementation(projects.types)
}
