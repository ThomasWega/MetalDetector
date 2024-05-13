plugins {
    `java-library`
    `maven-publish`
    id("io.freefair.lombok") version "8.0.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.wega"
version = "0.1.0b"
description = "Spigot plugin which detects selected items in inventory or ground"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly("org.spigotmc:spigot:1.17-R0.1-SNAPSHOT")
    compileOnly("me.wega:WegaToolkit:0.1b")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = rootProject.name
            version = version
            from(components["java"])
        }
    }
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()

        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
}