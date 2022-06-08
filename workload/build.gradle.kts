plugins {
    id("com.google.devtools.ksp")
    kotlin("jvm")
}

version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
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
    implementation(project(":kmap"))
    ksp(project(":kmap"))

    implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = "1.6.21")
    implementation(group = "org.mapstruct", name = "mapstruct", version = "1.3.1.Final")
    implementation(group = "org.springframework", name = "spring-context", version = "5.3.10")
}

ksp {
    arg("option1", "value1")
    arg("option2", "value2")
}
