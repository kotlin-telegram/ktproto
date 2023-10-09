plugins {
    id("kmp-library-convention")
    id("publication-convention")
}

version = libs.versions.ktprotoVersion.get()

dependencies {
    commonMainImplementation(projects.libs.stdlibExtensions)
}
