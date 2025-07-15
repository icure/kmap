import com.github.jk1.license.render.CsvReportRenderer
import com.github.jk1.license.render.ReportRenderer
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gitVersion)
    alias(libs.plugins.mavenRepository)
    alias(libs.plugins.licenseReport)
    `maven-publish`
}

licenseReport {
    renderers = arrayOf<ReportRenderer>(CsvReportRenderer())
}

val gitVersion: String? by project
group = "io.icure"
version = gitVersion ?: "0.0.1-SNAPSHOT"

dependencies {
    implementation(libs.bundles.kotlinPoet)
    implementation(libs.bundles.logback)
    implementation(libs.bundles.slf4j)

    implementation(libs.kotlinKsp)
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
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


