import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        load(FileInputStream(file))
    }
}

android {
    namespace = "com.iskanflix.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        val backendUrl = localProperties.getProperty("backendApiUrl") ?: "https://streamapi.abdiworks.my.id/"
        buildConfigField("String", "BACKEND_API_URL", "\"$backendUrl\"")

        val subdlKey = localProperties.getProperty("subdlApiKey")
            ?: "subdl__VuGBV3ExlH8tW68l4wVEIsk6Mfnaxq1_FiONFN8Yg0"
        buildConfigField("String", "SUBDL_API_KEY", "\"$subdlKey\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    buildFeatures {
        buildConfig = true
    }

    // Release builds ship a fixed backend URL and SubDL key: the Settings screen
    // hides them and ApiClient/SubdlRepository ignore user overrides.
    buildTypes {
        release {
            buildConfigField("boolean", "SETTINGS_LOCKED", "true")
        }
        debug {
            buildConfigField("boolean", "SETTINGS_LOCKED", "false")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Network & Serialization
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
}
