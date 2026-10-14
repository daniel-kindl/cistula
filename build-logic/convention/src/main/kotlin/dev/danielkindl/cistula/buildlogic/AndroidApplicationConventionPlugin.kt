package dev.danielkindl.cistula.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

/**
 * Shared configuration for Android application modules.
 *
 * Module-specific values (namespace, minSdk, packaging, dependencies) stay in each module build file.
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("com.autonomousapps.dependency-analysis")

            val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
            val jvmTargetVersion = catalog.intVersion("jvm-target")
            val appVersion = AppVersion.parse(providers.gradleProperty("cistula.version").get())

            extensions.configure<ApplicationExtension> {
                compileSdk {
                    version =
                        release(catalog.intVersion("android-compile-sdk")) {
                            minorApiLevel = 0
                        }
                }
                defaultConfig {
                    applicationId = APPLICATION_ID
                    targetSdk {
                        version = release(catalog.intVersion("android-target-sdk"))
                    }
                    versionCode = appVersion.versionCode
                    versionName = appVersion.name
                }
                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = false
                    }
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.toVersion(jvmTargetVersion)
                    targetCompatibility = JavaVersion.toVersion(jvmTargetVersion)
                }
                buildFeatures {
                    compose = true
                }
                lint {
                    abortOnError = true
                    warningsAsErrors = true
                    checkAllWarnings = true
                    checkReleaseBuilds = true
                    checkTestSources = true
                    explainIssues = true
                    lintConfig = rootProject.file("config/lint/lint.xml")
                }
            }

            constrainLintToolDependencies(catalog)

            extensions.configure<KotlinAndroidProjectExtension> {
                compilerOptions {
                    allWarningsAsErrors.set(true)
                    jvmTarget.set(JvmTarget.fromTarget(jvmTargetVersion.toString()))
                }
            }
        }
    }

    /**
     * The Android Lint tool brings dependencies with known advisories. Lint is build time only.
     * See build-tool-security-constraints in gradle/libs.versions.toml.
     */
    private fun Project.constrainLintToolDependencies(catalog: VersionCatalog) {
        val constraints = catalog.findBundle(SECURITY_CONSTRAINTS_BUNDLE).get()
        configurations.configureEach {
            if (name == LINT_TOOL_CONFIGURATION) {
                dependencyConstraints.addAllLater(
                    constraints.map { libraries -> libraries.map { project.dependencies.constraints.create(it) } },
                )
            }
        }
    }

    private fun VersionCatalog.intVersion(alias: String): Int =
        findVersion(alias)
            .orElseThrow { IllegalStateException("Missing version '$alias' in gradle/libs.versions.toml") }
            .requiredVersion
            .toInt()

    private companion object {
        const val APPLICATION_ID = "dev.danielkindl.cistula"
        const val LINT_TOOL_CONFIGURATION = "androidLintTool"
        const val SECURITY_CONSTRAINTS_BUNDLE = "build-tool-security-constraints"
    }
}
