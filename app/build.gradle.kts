plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10" // ✅ Added serialization plugin
}

android {
    namespace = "com.example.agrimata"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.agrimata"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core AndroidX dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.compose.runtime:runtime-livedata:1.7.8")


    // Jetpack Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // ViewModel for Jetpack Compose
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    // Supabase Dependencies
    val supabaseVersion = "1.3.2"
    implementation("io.github.jan-tennert.supabase:postgrest-kt:$supabaseVersion")
    implementation("io.github.jan-tennert.supabase:storage-kt:$supabaseVersion")
    implementation("io.github.jan-tennert.supabase:realtime-kt:$supabaseVersion")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:$supabaseVersion")
    implementation("io.github.jan-tennert.supabase:compose-auth:$supabaseVersion")
    implementation("io.github.jan-tennert.supabase:compose-auth-ui:$supabaseVersion")
    implementation("io.ktor:ktor-client-cio:2.3.4")

    // Navigation Components
    implementation("androidx.navigation:navigation-compose:2.8.9")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Extended Icons
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    // Networking (Retrofit for API calls)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Charts
    implementation("com.patrykandpatrick.vico:compose-m3:1.12.0")
    implementation("com.patrykandpatrick.vico:core:1.12.0")

    // Coil for Image Loading
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Kotlin serialization dependency
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    //location
    implementation("com.google.android.gms:play-services-location:21.3.0")
}

