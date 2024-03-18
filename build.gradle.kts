buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.2")
        classpath("com.google.gms:google-services:4.4.1")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
    }
}

plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.5" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.16" apply false
}

val coroutinesVersion = "1.8.0" // check version in app gradle file!

configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
        force("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$coroutinesVersion")
        force("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
        force("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    }
}