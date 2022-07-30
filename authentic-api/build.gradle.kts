version = "1.0-SNAPSHOT"

dependencies {
    compileOnly("org.jetbrains:annotations:23.0.0")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = rootProject.group.toString()
            version = version.toString()
            artifactId = "authentic-api"

            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "icewynd-repository"

            val releases = "https://maven.iwmc.pw/releases/"
            val snapshots = "https://maven.iwmc.pw/snapshots/"

            val finalUrl = if (rootProject.version.toString().endsWith("SNAPSHOT")) snapshots else releases

            url = uri(finalUrl)

            credentials {
                username = System.getenv("REPO_USERNAME")
                password = System.getenv("REPO_TOKEN")
            }
        }
    }
}