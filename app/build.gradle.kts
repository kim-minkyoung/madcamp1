plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
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
    viewBinding {
        enable = true
    }
}

//tab 2번째에서 갤러리 추가
dependencies {
    implementation ("com.google.android.material:material:1.4.0'")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.squareup.picasso:picasso:2.71828") // 이미지 로딩을 위한 라이브러리
}

dependencies {
    implementation ("com.github.bumptech.glide:glide:4.11.0")
    implementation(libs.androidx.runtime.saved.instance.state)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.11.0")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.github.bumptech.glide:recyclerview-integration:4.12.0")
    implementation ("com.github.bumptech.glide: annotations: 4.12. 0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
dependencies {
    implementation 'com.github.bumptech.glide:glide:4.12.0'
    implementation 'com.github.bumptech.glide:recyclerview-integration:4.12.0'
    implementation 'com.github.bumptech.glide:annotations:4.12.0'
}
