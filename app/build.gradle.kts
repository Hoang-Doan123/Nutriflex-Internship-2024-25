import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.example"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "FATSECRET_CLIENT_ID", "\"${localProperties.getProperty("fatsecret.clientId", "")}\"")
        buildConfigField("String", "FATSECRET_CLIENT_SECRET", "\"${localProperties.getProperty("fatsecret.clientSecret", "")}\"")
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

    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    // Retrofit for backend connection
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // osmdroid for OpenStreetMap
    implementation("org.osmdroid:osmdroid-android:6.1.16") {
        exclude(group = "com.mcxiaoke.volley", module = "library")
    }

    // FusedLocationProviderClient for accurate location
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // FatSecret API
    implementation("com.fatsecret4j:fatsecret-platform:2.0") {
        exclude(group = "com.mcxiaoke.volley", module = "library")
    }

    // HTTP client
    implementation("com.android.volley:volley:1.2.1")

    implementation(libs.swiperefreshlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}