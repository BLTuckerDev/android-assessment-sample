plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.cricut.androidassessment"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cricut.androidassessment"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

detekt {
    toolVersion = "1.23.8"
    config.setFrom(file("../config/detekt/detekt.yml"))
    baseline = file("$rootDir/config/detekt/baseline.xml")
    buildUponDefaultConfig = true
    autoCorrect = true
}

dependencies {
    implementation(libs.androidx.core.ktx)


    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.activity)
    implementation(libs.compose.animation)
    implementation(libs.compose.material3)
    implementation(libs.compose.icons.extended)
    implementation(libs.compose.tooling.preview)
    debugImplementation(libs.compose.tooling)


    // Detekt
    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.detekt.compose)


    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Lifecycle
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.savedstate)



    // Navigation
    implementation(libs.navigation.compose)
    implementation(libs.hilt.navigation.compose)


    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.arch.core.testing)
    testImplementation(libs.lifecycle.runtime.testing)
    testImplementation(libs.mockk)
    testImplementation(libs.room.testing)

    // Testing - Android tests
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso)
    androidTestImplementation(libs.arch.core.testing)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.room.testing)
    androidTestImplementation(libs.coroutines.test)

    debugImplementation(libs.compose.tooling)
}