import dev.kikugie.stonecutter.settings.StonecutterSettingsExtension

pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        maven {
            name = "Forge"
            url = uri("https://maven.minecraftforge.net/")
        }
        maven {
            name = "KikuGie"
            url = uri("https://maven.kikugie.dev/snapshots")
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7.5"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "Durability101"

extensions.configure<StonecutterSettingsExtension>("stonecutter") {
    centralScript.set("version.gradle.kts")
    create(rootProject) {
        vers("fabric-1.14-4", "1.14.4")
        vers("fabric-1.15-2", "1.15.2")
        vers("fabric-1.16-5", "1.16.5")
        vers("fabric-1.17-1", "1.17.1")
        vers("fabric-1.18-2", "1.18.2")
        vers("fabric-1.19-2", "1.19.2")
        vers("fabric-1.19.3", "1.19.3")
        vers("fabric-1.19.4", "1.19.4")
        vers("fabric-1.20-6", "1.20.6")
        vers("fabric-1.21-1", "1.21")
        vers("fabric-1.21.2-5", "1.21.2")
        vers("fabric-1.21.6-11", "1.21.6")
        vers("forge-1.12-2", "1.12.2")
        vers("forge-1.13.2", "1.13.2")
        vers("forge-1.14.2-4", "1.14.4")
        vers("forge-1.15-2", "1.15.2")
        vers("forge-1.16.1-5", "1.16.5")
        vers("forge-1.17.1", "1.17.1")
        vers("forge-1.18-2", "1.18.2")
        vers("forge-1.19-2", "1.19.2")
        vers("forge-1.19.2", "1.19.2")
        vers("forge-1.19.3", "1.19.3")
        vers("forge-1.19.4", "1.19.4")
        vers("forge-1.20-6", "1.20.4")
        vers("forge-1.21-1", "1.21")
        vers("forge-1.21.3-5", "1.21.3")
        vers("forge-1.21.6-8", "1.21.6")
        vers("forge-1.21.9", "1.21.9")
    }
}
