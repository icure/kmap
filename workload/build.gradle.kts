import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
}

version = "1.0-SNAPSHOT"

sourceSets {
    main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}

dependencies {
    implementation(project(":kmap"))
    ksp(project(":kmap"))

    implementation(libs.kotlinReflect)
    implementation(libs.springContext)
}


kotlin {
    jvmToolchain(21)
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        allWarningsAsErrors = true
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

}

ksp {
    arg("option1", "value1")
    arg("option2", "value2")
}
