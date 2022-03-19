plugins {
    id("com.google.devtools.ksp")
    kotlin("jvm")
}

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    sourceSets {
        main {
            kotlin.srcDir("build/generated/ksp/main/kotlin")
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":mapper-processor"))
    ksp(project(":mapper-processor"))

    implementation(group = "org.mapstruct", name = "mapstruct", version = "1.3.1.Final")
}

ksp {
    arg("option1", "value1")
    arg("option2", "value2")
}
