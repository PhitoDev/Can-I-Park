import org.jetbrains.kotlin.utils.addToStdlib.butIf
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.kotlinSerialization)
}

buildConfig {
    var apiKey = System.getenv("API_KEY") ?: ""
    butIf(apiKey.isEmpty()) {
        try {
            val properties = Properties()
            properties.load(rootProject.file("local.properties").reader())
            apiKey = properties.getProperty("apiKey")
        } catch (e: Exception) {
            println("API_KEY not found in local.properties")

        }
    }
    buildConfigField("String", "API_KEY", apiKey)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.bundles.ktor.client)
            implementation(libs.bundles.koin)
        }
        androidMain.dependencies {
            implementation(libs.mlkit)
            implementation(libs.google.ai)
            implementation(libs.bundles.cameraX)
        }
    }
}

android {
    namespace = "com.dugue.canipark.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
