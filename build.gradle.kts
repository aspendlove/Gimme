import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.jetbrains.kotlin.plugin.compose)
    alias(libs.plugins.serialization)
}

group = "com.aspendlove"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

kotlin {
    sourceSets {
        dependencies {
            implementation(compose.components.resources)
        }
    }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(libs.sqlite.jdbc)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.voyager.navigator)
    implementation(libs.voyager.transitions)
    implementation(libs.kotlinx.datetime)
    implementation(libs.feather)
    implementation(compose.components.resources)
    testImplementation(libs.kotlin.test)
}

val gimmeVersion: String by project

compose.desktop {

    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.AppImage, TargetFormat.Msi)
            packageName = "Gimme"
            packageVersion = gimmeVersion
            version = gimmeVersion
            includeAllModules = true
            windows {
                shortcut = true
                menu = true
                this.perUserInstall = true
            }
        }

        buildTypes.release.proguard {
            configurationFiles.from(project.file("compose-desktop.pro"))
        }
    }
}

