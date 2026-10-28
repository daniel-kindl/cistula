plugins {
    alias(libs.plugins.cistula.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

room {
    schemaDirectory("$projectDir/schemas")
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
    implementation(libs.androidx.room.runtime)
    implementation(libs.kotlinx.coroutines.core)
    ksp(libs.androidx.room.compiler)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.core)
    constraints {
        implementation(libs.androidx.lifecycle.runtime) {
            because("Lifecycle 2.9.4 Lint checks use an API removed from AGP 9.5.")
        }
    }
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
