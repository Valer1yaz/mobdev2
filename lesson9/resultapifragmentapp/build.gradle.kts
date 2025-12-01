plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "ru.mirea.zhemaitisvs.resultapifragmentapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "ru.mirea.zhemaitisvs.resultapifragmentapp"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    var fragment_version = "1.8.5"
    implementation ("androidx.fragment:fragment:$fragment_version")
    implementation ("com.google.android.material:material:1.9.0")
}