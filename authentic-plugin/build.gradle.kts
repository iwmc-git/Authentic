import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2" apply(true)
}

dependencies {
    implementation(project(":authentic-api"))

    implementation("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")
    compileOnly("pw.iwmc.libman:libman-api:1.0.6")

    compileOnly("pw.iwmc.noelle:standalone-configuration-common:0.2.2-SNAPSHOT")
    compileOnly("pw.iwmc.noelle:standalone-configuration-yaml:0.2.2-SNAPSHOT")

    compileOnly("pw.iwmc.noelle:standalone-database-common:0.2.2-SNAPSHOT")
    compileOnly("pw.iwmc.noelle:standalone-database-mariadb:0.2.2-SNAPSHOT")
    compileOnly("pw.iwmc.noelle:standalone-database-h2:0.2.2-SNAPSHOT")

    compileOnly("pw.iwmc.noelle:velocity-languages:0.2.2-SNAPSHOT")
    compileOnly("pw.iwmc.noelle:common-languages:0.2.2-SNAPSHOT")

    compileOnly("com.github.ben-manes.caffeine:caffeine:3.1.1")
}

tasks {
    withType(ShadowJar::class.java) {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("")
        archiveVersion.set(rootProject.version.toString())
    }

    withType(ProcessResources::class.java) {
        val map = mutableMapOf<String, Any>()

        map["id"] = rootProject.name.toLowerCase()
        map["name"] = rootProject.name
        map["version"] = rootProject.version.toString()
        map["description"] = rootProject.description.toString()

        filesMatching("velocity-plugin.json") {
            expand(map)
        }
    }
}