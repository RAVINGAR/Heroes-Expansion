plugins {
    id("idea")
    id("java")
    id("java-library")
}

group = "com.ravingarinc.papi.expansion.heroes"
version = "1.9.30"

repositories {
    gradlePluginPortal()
    mavenCentral()

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")

    maven("https://nexus.hc.to/content/repositories/pub_releases/")

    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

val exposedVersion = "0.40.1"

dependencies {
    compileOnly("org.jetbrains:annotations:23.1.0")
    compileOnly("com.herocraftonline.heroes:Heroes:1.9.30-RELEASE")
    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.3")
}

tasks {

    idea {
        module {
            isDownloadJavadoc = true
            isDownloadSources = true
        }
    }
}
