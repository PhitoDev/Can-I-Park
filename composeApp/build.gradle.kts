import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
}

kotlin {
    androidTarget {
        compilations.all {
        }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    sourceSets {
        
        androidMain.dependencies {
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.material3.android)
            implementation(libs.androidx.ui.unit)
            implementation(libs.androidx.datastore.core.android)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.bundles.androidx)
            implementation(libs.bundles.datastore)
            implementation(libs.koin.android)
            implementation(libs.bundles.cameraX)
            implementation(libs.play.services.admob)
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation("com.google.firebase:firebase-crashlytics")
            implementation("com.google.firebase:firebase-analytics")
        }
        commonMain.dependencies {
            implementation(libs.kermit)
            implementation(projects.shared)
        }
    }
}

android {
    namespace = "com.dugue.canipark"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    var releaseStoreFile =  System.getenv("RELEASE_STORE_FILE") ?: ""
    var releaseKeyPassword =   System.getenv("RELEASE_KEY_PASSWORD") ?: ""
    var releaseKeyAlias = System.getenv("RELEASE_KEY_ALIAS") ?: ""
    var releaseStorePassword = System.getenv("RELEASE_STORE_PASSWORD") ?: ""

    try {
        val properties = Properties()
        properties.load(rootProject.file("local.properties").reader())
        releaseStoreFile = properties.getProperty("storeFile")
        releaseKeyPassword = properties.getProperty("keyPassword")
        releaseKeyAlias = properties.getProperty("keyAlias")
        releaseStorePassword = properties.getProperty("storePassword")
    } catch (e: Exception) {
        println("Warning: local.properties not found. This is fine if this is a CI build.")
    }

    defaultConfig {
        applicationId = "com.dugue.canipark"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 15
        versionName = "1.5.7"
    }
    signingConfigs {
        create("release") {
            val storeCredentials = listOf(
                releaseStoreFile,
                releaseKeyAlias,
                releaseStorePassword,
                releaseKeyPassword
            )
            if (storeCredentials.all { it.isNotEmpty() }) {
                storeFile = file(releaseStoreFile)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}

