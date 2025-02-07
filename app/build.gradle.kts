plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") // Kotlin desteği için
    id("com.google.gms.google-services") // Firebase için gerekli
}

android {
    namespace = "com.example.calorimeter5"
    compileSdk = 34 // En son API seviyesi

    defaultConfig {
        applicationId = "com.example.calorimeter5"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Firebase BOM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-auth")

    // AndroidX Kütüphaneleri
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.10.1")

    // Material Components
    implementation("com.google.android.material:material:1.9.0")

    // Picasso (Görseller için)
    implementation("com.squareup.picasso:picasso:2.8")

    // Test Kütüphaneleri
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

