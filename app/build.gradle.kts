plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("plugin.serialization")
}

android {
    namespace = "me.paladin.barsurasp"
    compileSdk = 34

    defaultConfig {
        applicationId = "me.paladin.barsurasp"
        minSdk = 22
        targetSdk = 33
        versionCode = 4
        versionName = "1.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    kotlinOptions {
        jvmTarget = "1.8"

        // Disable ExperimentalMaterial3Api warning
        freeCompilerArgs += "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.ktor.core)
    implementation(libs.ktor.android)
    implementation(libs.jsoup)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.material)
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    // Jetpack
    implementation(libs.core.ktx)
    implementation(libs.datastore)
    implementation(libs.lifecycle)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("com.yandex.android:mobileads:6.1.0")
    implementation("androidx.glance:glance-appwidget:1.0.0")
    implementation("androidx.glance:glance-material3:1.0.0")
}