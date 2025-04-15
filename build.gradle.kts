import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow") version "8.3.6"
}

group = "dev.digitality"
description = "DigitalGUI"
version = "1.1.3"

java.toolchain {
    languageVersion = JavaLanguageVersion.of(11)
    vendor = JvmVendorSpec.ADOPTIUM
}

repositories {
    mavenCentral()

    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://repo.codemc.org/repository/maven-public/")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    implementation("de.tr7zw:item-nbt-api:2.14.1")

    compileOnly("org.jetbrains:annotations:26.0.2")
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<ShadowJar> {
        minimize()

        relocate("de.tr7zw.changeme.nbtapi", "dev.digitality.digitalgui.nbtapi")

        archiveFileName = "${project.name}.jar"
        archiveClassifier = null
    }

    register("sourceJar", Jar::class) {
        archiveClassifier = "sources"

        from(sourceSets.main.get().allSource)
    }
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.gold-zone.cz/releases")

            credentials {
                username = (project.findProperty("goldzoneRepo.username") ?: System.getenv("GOLDZONE_REPO_USERNAME")) as String?
                password = (project.findProperty("goldzoneRepo.password") ?: System.getenv("GOLDZONE_REPO_PASSWORD")) as String?
            }

            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        create<MavenPublication>("shadow") {
            groupId = project.group as String?
            artifactId = project.name.lowercase()
            version = project.version as String?

            artifact(tasks["sourceJar"])

            from(components["shadow"])
        }
    }
}