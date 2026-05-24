import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
}

android {
    compileSdk = 36
    namespace = "com.example.chitchatapp"

    defaultConfig {
        applicationId = "com.example.chitchatapp"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val localProps = Properties().apply {
                val localPropsFile = rootProject.file("local.properties")
                if (localPropsFile.exists()) {
                    localPropsFile.inputStream().use { load(it) }
                }
            }

            fun getEnvOrLocalProp(key: String): String? =
                System.getenv(key) ?: localProps.getProperty(key)

            val keystorePath = getEnvOrLocalProp("RELEASE_KEYSTORE_PATH")
            if (keystorePath != null) {
                storeFile = rootProject.file(keystorePath)
                storePassword = getEnvOrLocalProp("RELEASE_STORE_PASSWORD")
                keyAlias = getEnvOrLocalProp("RELEASE_KEY_ALIAS")
                keyPassword = getEnvOrLocalProp("RELEASE_KEY_PASSWORD")
            } else {
                println("⚠️ RELEASE_KEYSTORE_PATH is missing — skipping release signing")
            }
        }
    }

    buildTypes {
        debug {
            versionNameSuffix = "-debug"
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        dataBinding = true
    }

    buildToolsVersion = "36.0.0"
}

// Rename APK output files per variant
androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            output.outputFileName.set("app-${variant.name}.apk")
        }
    }
}

dependencies {
    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ui.auth)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)

    // Glide
    implementation(libs.glide)

    // uCrop
    implementation(libs.ucrop)

    // Lottie
    implementation(libs.lottie)

    // Zoomable ImageView
    implementation(libs.touch.image.view)

    // Volley
    implementation(libs.volley)
}








