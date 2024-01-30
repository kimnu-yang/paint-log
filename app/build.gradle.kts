import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

//    implementation("com.kakao.sdk:v2-all:2.19.0") // 전체 모듈 설치, 2.11.0 버전부터 지원
    implementation("com.kakao.sdk:v2-user:2.19.0") // 카카오 로그인
//    implementation("com.kakao.sdk:v2-cert:2.19.0") // 카카오 인증서비스

    // http request
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences-core:1.0.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Room
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    implementation("com.github.prolificinteractive:material-calendarview:2.0.0")
    implementation("com.jakewharton.threetenabp:threetenabp:1.1.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
}

android {
    namespace = "com.diary.paintlog"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.diary.paintlog"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 프로젝트 내에서 언제든지 사용이 가능한 변수들이다.
        // 타입 - 키 - 값으로 저장된다.
        buildConfigField("String", "KAKAO_NATIVE_KEY", getLocalProperties("KAKAO_NATIVE_KEY"))
        buildConfigField("String", "API_SERVER_ADDRESS", getLocalProperties("API_SERVER_ADDRESS"))
        manifestPlaceholders["KAKAO_NATIVE_KEY"] =
            getLocalProperties("KAKAO_NATIVE_KEY").replace("\"", "")
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
        buildConfig = true
    }
}

// local.properties에 정의된 값을 가져오는 함수
fun getLocalProperties(propertyKey: String): String {
    return gradleLocalProperties(rootDir).getProperty(propertyKey)
}