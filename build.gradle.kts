plugins {
    id("java")
    id("java-library")
    id("maven-publish")
}

group = "com.bravos.steak"
version = "1.0.5"

publishing {
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/bravos2k5/steak-utils")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {

}
