plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.rellotgejais"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.rellotgejais"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)

    // API
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.lyfecycleandroid)
    implementation(libs.http.interceptor)
    implementation(libs.glide)
    kapt(libs.glide.compiler)

    // socket
    implementation("io.socket:socket.io-client:2.0.1") {
        exclude(group = "org.json", module = "json")
    }

    // UI
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("net.orandja.shadowlayout:shadowlayout:1.0.1")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    //para el crono en directo
    implementation ("androidx.fragment:fragment-ktx:1.6.2")

}