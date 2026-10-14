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
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Shared synthetic data for JVM tests and instrumentation tests.
    testFixtures {
        enable = true
    }

    buildTypes {
        getByName("debug") {
            // Reports only. There is no coverage percentage threshold.
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }

    testOptions {
        animationsDisabled = true
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

    testImplementation(libs.junit)
    // The Compose compiler plugin checks every Kotlin compilation, including tests that
    // do not call Compose. compileOnly keeps the runtime off the test runtime classpath.
    testCompileOnly(composeBom)
    testCompileOnly(libs.androidx.compose.runtime)
    testFixturesCompileOnly(composeBom)
    testFixturesCompileOnly(libs.androidx.compose.runtime)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
