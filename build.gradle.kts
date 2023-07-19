import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    java
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.5.0"
    id("app.cash.sqldelight") version "2.0.0-alpha05"
}

group = "com.aspendlove"
version = "1.0"
dependencies {

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    val voyagerVersion = "1.0.0-rc05"

    // Multiplatform

    // Navigator
    implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")

    // BottomSheetNavigator
    implementation("cafe.adriel.voyager:voyager-bottom-sheet-navigator:$voyagerVersion")

    // TabNavigator
    implementation("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")

    // Transitions
    implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")
}

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}


kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("com.github.jhonnymertz:java-wkhtmltopdf-wrapper:1.1.15-RELEASE")
                implementation("com.darkrockstudios:mpfilepicker:1.2.0")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.AppImage)
            packageName = "Gimme"
            packageVersion = "1.0.0"
        }
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.gimme")
        }
    }
}
