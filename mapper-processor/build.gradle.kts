val kotlinVersion: String by project
val kspVersion: String by project

val kpv = "1.10.2"

plugins {
    kotlin("jvm")
}

group = "com.example"
version = "1.0-SNAPSHOT"

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

