import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
}

val jvmTargetVersion =
    libs.versions.jvm.target
        .get()

kotlin {
    compilerOptions {
        allWarningsAsErrors.set(true)
        jvmTarget.set(JvmTarget.fromTarget(jvmTargetVersion))
    }
}

java {
    sourceCompatibility = JavaVersion.toVersion(jvmTargetVersion)
    targetCompatibility = JavaVersion.toVersion(jvmTargetVersion)
}

dependencies {
    compileOnly(gradleKotlinDsl())
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.compose.compiler.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id =
                libs.plugins.cistula.android.application
                    .get()
                    .pluginId
            implementationClass = "dev.danielkindl.cistula.buildlogic.AndroidApplicationConventionPlugin"
        }
    }
}
