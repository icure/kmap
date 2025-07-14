pluginManagement {
    val kotlinVersion: String by settings
    val kspVersion: String by settings
    plugins {
        id("com.google.devtools.ksp") version kspVersion
        kotlin("jvm") version kotlinVersion
    }
    repositories {
        gradlePluginPortal()
        maven { url = uri("https://maven.taktik.be/content/groups/public") }
    }
}

rootProject.name = "kmap-processor"

include(":workload")
include(":kmap")
