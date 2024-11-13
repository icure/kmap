pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://maven.taktik.be/content/groups/public") }
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://maven.taktik.be/content/groups/public") }
    }
}


rootProject.name = "kmap-processor"

include(":workload")
include(":kmap")
