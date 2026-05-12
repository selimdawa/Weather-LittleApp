plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.littleapp.weather"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.littleapp.weather"
        minSdk = 24
        targetSdk = 36
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
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation("androidx.fragment:fragment-ktx:1.5.5")
    implementation(libs.androidx.preference.ktx)           //Shared Preference
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //Layout
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.cardview)
    //Other's
    implementation(libs.picasso)                           //Picasso
    implementation(libs.volley)                            //Volley
    implementation(libs.play.services.location)            //Weather Location
    implementation(libs.play.services.location.license)
    implementation(libs.timber)                            //Timber Log

    /*

    // reflection-free flavor
    implementation 'com.github.kirich1409:viewbindingpropertydelegate-noreflection:1.5.6'

    // coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4'

    //retrofit,OkHttp
    implementation 'com.squareup.retrofit2:retrofit:2.5.0'

    //gson
    implementation 'com.google.code.gson:gson:2.8.9'

    //retrofit converter-gson
    implementation 'com.squareup.retrofit2:converter-gson:2.3.0'

    //leakCanary
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.7")
    releaseImplementation 'com.squareup.leakcanary:leakcanary-android-no-op:1.5.4'

    implementation 'androidx.core:core-ktx:1.9.0'
    implementation 'androidx.appcompat:appcompat:1.6.0'

    implementation 'com.android.volley:volley:1.2.1'
    implementation 'com.squareup.picasso:picasso:2.71828'
    implementation 'androidx.fragment:fragment-ktx:1.5.5'
    implementation 'com.google.android.gms:play-services-location:21.0.1'*/
}