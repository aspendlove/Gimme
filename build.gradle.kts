import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    java
    kotlin("jvm") version "1.9.23"
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.5.0"
}

val exposedVersion: String by project
val jdbcVersion: String by project
val serializationVersion: String by project
val voyagerVersion: String by project
group = "com.aspendlove"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(compose.desktop.currentOs)
    implementation("org.xerial:sqlite-jdbc:$jdbcVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
    implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
    implementation("br.com.devsrsouza.compose.icons:feather:1.1.0")
}


//kotlin {
//    jvm {
//        jvmToolchain(17)
//        withJava()
//    }
//    sourceSets {
//        val jvmMain by getting {
//            dependencies {
//                implementation(compose.desktop.currentOs)
//            }
//        }
//        val jvmTest by getting {
//            dependencies {
//                implementation(kotlin("test-junit"))
//            }
//        }
//    }
//}

val gimmeVersion: String by project

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.AppImage)
            packageName = "Gimme"
            packageVersion = gimmeVersion
            version = gimmeVersion
            includeAllModules = true
        }

        buildTypes.release.proguard {
            configurationFiles.from(project.file("compose-desktop.pro"))

        }
    }
}
