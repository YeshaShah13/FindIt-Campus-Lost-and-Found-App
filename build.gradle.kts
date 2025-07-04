buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.2")// your existing one
        classpath("com.google.gms:google-services:4.4.2")// 🔥 ADD THIS LINE
    }
}
plugins {
    alias(libs.plugins.google.gms.google.services) apply false
}
