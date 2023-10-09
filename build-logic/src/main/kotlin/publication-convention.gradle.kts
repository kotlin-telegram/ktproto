plugins {
    id("org.gradle.maven-publish")
}

group = "me.y9san9.ktproto"

publishing {
    repositories {
        maven {
            name = "ktproto"
            url = uri("https://maven.pkg.github.com/kotlin-telegram/ktproto")
            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications.withType<MavenPublication> {
        versionFromProperties { version ->
            this.version = version
        }
    }
}
