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
    //viewBinding 활성화
    viewBinding {
        enable = true
    }
    buildFeatures {
        dataBinding = true
    }

}
//tab 2번째에서 갤러리 추가
dependencies {
    // 네이버 지도 SDK
    implementation("com.naver.maps:map-sdk:3.18.0")
    implementation("com.android.support:animated-vector-drawable:28.0.0")
    implementation("com.android.support:support-media-compat:28.0.0")

    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.squareup.picasso:picasso:2.71828") // 이미지 로딩을 위한 라이브러리

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")

    //viewModelScope 사용 위해 lifecycle 종속성 추가
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")

    // 다음과 같이 libs 안에 있는 라이브러리들을 참조하는 경우
    implementation(libs.androidx.runtime.saved.instance.state)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation ("androidx.fragment:fragment-ktx:1.3.6")
    // 테스트 관련 종속성
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //위치 권한 관련
    implementation ("com.google.android.gms:play-services-location:21.0.1")
}
