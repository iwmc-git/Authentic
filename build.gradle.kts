plugins {
    id("java-library")
}

allprojects {
    apply(plugin = "java-library")

    group = "pw.iwmc.authentic"
    version = "0.0.1-SNAPSHOT"
    description = "Fast and multifunctional authorization plugin."

    tasks {
        withType(JavaCompile::class.java) {
            options.release.set(17)
            options.encoding = "UTF-8"
        }
    }

    java {
        withSourcesJar()
    }

    repositories {
        mavenCentral()

        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://repo.spongepowered.org/repository/maven-public/") }
        maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
        maven { url = uri("https://repo.opencollab.dev/maven-snapshots/") }
        maven { url = uri("https://repo.panda-lang.org/releases") }
        maven { url = uri("https://maven.elytrium.net/repo/") }
        maven { url = uri("https://maven.iwmc.pw/snapshots/") }
        maven { url = uri("https://maven.iwmc.pw/releases/") }
    }
}
