plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.giwih.essentialremap"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.giwih.essentialremap"
        minSdk = 26
        targetSdk = 34
        versionCode = (project.findProperty("appVerCode") as String).toInt()
        versionName = project.findProperty("appVerName") as String
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // API Xposed (LSPosed)
    compileOnly("de.robv.android.xposed:api:82")
}
