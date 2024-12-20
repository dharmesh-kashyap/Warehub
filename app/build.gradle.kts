plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.warehub"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.warehub"
        minSdk = 27
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.analytics.impl)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.opencsv:opencsv:5.5.2")
    implementation ("androidx.cardview:cardview:1.0.0")



    implementation ("com.itextpdf:itext7-core:7.1.14")
    implementation("androidx.recyclerview:recyclerview:1.3.0")

}