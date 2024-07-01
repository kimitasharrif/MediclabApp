plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.sherrif.mediclab"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sherrif.mediclab"
        minSdk = 28
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation ("androidx.appcompat:appcompat:1.3.1")
    implementation ("androidx.core:core-ktx:1.6.0")
    implementation ("com.google.android.material:material:1.4.0")

    implementation(libs.androidx.core.ktx)



    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    //LoopJ for API Connections
    implementation ("com.loopj.android:android-async-http:1.4.9")
    // For JSON Conversions, From JSONArray to ArrayList
    implementation ("com.google.code.gson:gson:2.8.7")
    //For Swipe Refresh in Recycler View
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    //For making rounded circular imageView
    implementation("de.hdodenhof:circleimageview:3.0.1")
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}