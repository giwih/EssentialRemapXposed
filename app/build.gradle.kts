plugins {
    id("com.android.application")
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
        buildConfigField("String", "TARGET_PKG", "${project.findProperty("TARGET_PACKAGE")}")
        buildConfigField("String", "TARGET_CLS", "${project.findProperty("TARGET_CLASS")}")
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
    // API Xposed (LSPosed)
    compileOnly("de.robv.android.xposed:api:82")
}
