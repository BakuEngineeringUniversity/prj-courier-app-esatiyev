plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.iko.android.courier"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.iko.android.courier"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true

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
//    buildFeatures {
//        viewBinding = true
//        dataBinding = true
//    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildToolsVersion = rootProject.extra["buildToolsVersion1"] as String
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

//    implementation("androidx.customview:customview:1.1.0")
//    implementation("com.ramotion.foldingcell:folding-cell:1.2.3")
    implementation("com.facebook.fresco:fresco:1.10.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.google.android.material:material:1.11.0")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // For Gson converter
    implementation("com.google.code.gson:gson:2.9.0")


    implementation("androidx.recyclerview:recyclerview:1.3.2")

//    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0"
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // google maps
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation("com.google.android.libraries.places:places:3.3.0")

    implementation("androidx.core:core-splashscreen:1.0.1")
}