val kotlinVersion: String by project
val kspVersion: String by project

val kpv = "1.10.2"

plugins {
    kotlin("jvm")
}

buildscript {
    repositories {
        mavenCentral()
        maven { url = uri("https://maven.taktik.be/content/groups/public") }
    }
    dependencies {
        classpath("com.taktik.gradle:gradle-plugin-git-version:2.0.2")
        classpath("com.taktik.gradle:gradle-plugin-maven-repository:1.0.2")
    }
}

apply(plugin = "git-version")
apply(plugin = "maven-repository")
val gitVersion: String? by project

group = "io.icure"
version = gitVersion ?: "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("com.squareup:kotlinpoet:$kpv")
    implementation("com.squareup:kotlinpoet-ksp:$kpv")
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
    implementation(group = "org.mapstruct", name = "mapstruct", version = "1.3.1.Final")

    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.6")
    implementation(group = "ch.qos.logback", name = "logback-access", version = "1.2.6")

    implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.32")
    implementation(group = "org.slf4j", name = "jul-to-slf4j", version = "1.7.32")
    implementation(group = "org.slf4j", name = "jcl-over-slf4j", version = "1.7.32")
    implementation(group = "org.slf4j", name = "log4j-over-slf4j", version = "1.7.32")
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

