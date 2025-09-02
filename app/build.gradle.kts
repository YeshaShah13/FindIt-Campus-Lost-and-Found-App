plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // ✅ Required for Firebase services
}

android {
    namespace = "com.example.findit"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.findit"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // UI
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")



    // Firebase Core Services
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-firestore:25.1.4")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-messaging:24.1.2") // ✅ PUSH NOTIFICATIONS
    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation("com.squareup.picasso:picasso:2.8")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.0.0")


    // Room (if still needed locally)
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // Image picker & Glide
    implementation("com.github.dhaval2404:imagepicker:2.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.2")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Firebase BOM (manages versions automatically)
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

// Firestore
    implementation ("com.google.firebase:firebase-firestore")

// Required
    implementation("com.google.firebase:firebase-analytics")



}
apply(plugin = "com.google.gms.google-services")
