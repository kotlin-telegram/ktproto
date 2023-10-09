enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        maven {
            name = "koTL GitHub"
            url = uri("https://maven.pkg.github.com/kotlin-telegram/koTL")
            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

includeBuild("build-logic")

include(
    ":types",
    ":transport",
    ":session",
    ":client",
    ":client:ktor",
    ":libs:stdlib-extensions",
    ":libs:io",
    ":libs:crypto"
)

rootProject.name = "ktproto"
