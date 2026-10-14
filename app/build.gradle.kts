plugins {
    alias(libs.plugins.cistula.android.application)
}

android {
    namespace = "dev.danielkindl.cistula"
    defaultConfig {
        minSdk {
            version =
                release(
                    libs.versions.app.min.sdk
                        .get()
                        .toInt(),
                )
        }
    }

    packaging {
        jniLibs {
            keepDebugSymbols += "**/libandroidx.graphics.path.so"
        }
    }
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(libs.androidx.activity.compose)
    implementation(composeBom)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
