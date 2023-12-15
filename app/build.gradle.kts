plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.adviser"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.adviser"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        renderscriptTargetApi = 21
        renderscriptSupportModeBlasEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        mlModelBinding = true
    }

    aaptOptions {
        noCompress ("tflite")
    }

    externalNativeBuild {
        ndkBuild {
            path ("src/main/jni/Android.mk")
        }
    }

}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.bluetooth:bluetooth:1.0.0-alpha01")
    implementation(project(mapOf("path" to ":OpenCV")))
    implementation("org.jetbrains:annotations:15.0")
    implementation(files("libs/naveridlogin-android-sdk-4.4.1.aar"))
    implementation("org.tensorflow:tensorflow-lite-support:0.1.0")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    implementation("com.kakao.maps.open:android:2.6.0")
    implementation(files("libs/libDaumMapAndroid.jar"))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.retrofit2:converter-scalars:2.9.0")

    implementation("org.jetbrains:annotations:15.0")
    implementation("commons-io:commons-io:2.11.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("me.relex:circleindicator:2.1.6")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment:2.7.5")
    implementation("androidx.navigation:navigation-ui:2.7.5")
    implementation("com.kakao.sdk:v2-all:2.17.0")
    implementation("com.kakao.sdk:v2-user:2.17.0")
    implementation("com.kakao.sdk:v2-talk:2.17.0")
    implementation("com.kakao.sdk:v2-story:2.17.0")
    implementation("com.kakao.sdk:v2-share:2.17.0")
    implementation("com.kakao.sdk:v2-friend:2.17.0")
    implementation("com.kakao.sdk:v2-navi:2.17.0")
    implementation("com.kakao.sdk:v2-cert:2.17.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("org.tensorflow:tensorflow-lite:+")
    implementation (project(":OpenCV"))



}