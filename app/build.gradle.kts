import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.compose)
}

// Repositories are managed in settings.gradle.kts

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.ui)
    implementation(compose.foundation)
    
    // Dependencies
    implementation(libs.poi.ooxml)
    implementation(libs.kotlinx.serialization)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

compose.desktop {
    application {
        mainClass = "com.havrutot.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Exe)
            packageName = "HavrutotMatcher"
            packageVersion = "1.0.0"
            windows {
                iconFile.set(project.file("icon.ico"))
            }
        }
    }
}
