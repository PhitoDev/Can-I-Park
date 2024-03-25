import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    
    sourceSets {
        
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.bundles.androidx)
            implementation(libs.koin.android)
            implementation(libs.bundles.cameraX)
            implementation(libs.play.services.admob)
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation("com.google.firebase:firebase-crashlytics")
            implementation("com.google.firebase:firebase-analytics")
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(libs.kermit)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
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
        versionCode = 7
        versionName = "1.4"
    }
    signingConfigs {
        create("release") {
            if (releaseStoreFile.isNotEmpty() && releaseKeyAlias.isNotEmpty() && releaseStorePassword.isNotEmpty() && releaseKeyPassword.isNotEmpty()) {
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
dependencies {
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.material3.android)
}

