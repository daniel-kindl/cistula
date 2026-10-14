plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.dependency.analysis)
}

spotless {
    val ktlintVersion = libs.versions.ktlint.get()
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**")
        ktlint(ktlintVersion)
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        targetExclude("**/build/**")
        ktlint(ktlintVersion)
    }
}

dependencyAnalysis {
    issues {
        all {
            onUnusedDependencies { severity("fail") }
            onIncorrectConfiguration { severity("fail") }
            onCompileOnly { severity("fail") }
            onRuntimeOnly { severity("fail") }
            onUnusedAnnotationProcessors { severity("fail") }
            onRedundantPlugins { severity("fail") }
            onDuplicateClassWarnings { severity("fail") }
            // Compose artifacts arrive through umbrella libraries (material3) whose
            // versions the BOM or the library itself aligns. Declaring each transitive artifact would
            // duplicate those versions in the catalog. See docs/decisions/0003-build-standards-and-verification.md.
            onUsedTransitiveDependencies { severity("ignore") }
        }
    }
}

// Detekt runs through its official CLI. The 1.23 Gradle plugin uses Gradle APIs that are deprecated
// in Gradle 9, and this build fails on any deprecation (org.gradle.warning.mode=fail).
val detektCliDependencies = configurations.dependencyScope("detektCliDependencies")
val detektCli = configurations.resolvable("detektCli") { extendsFrom(detektCliDependencies.get()) }

dependencies {
    detektCliDependencies(libs.detekt.cli)
}

val detektSources =
    listOf("app", "build-logic/convention")
        .flatMap { module ->
            listOf("main", "test", "androidTest").flatMap { set -> listOf("$module/src/$set/kotlin", "$module/src/$set/java") }
        }.map(::file)
        .filter(File::isDirectory)

tasks.register<JavaExec>("detekt") {
    group = "verification"
    description = "Runs Detekt static analysis on all Kotlin sources."
    val configFile = file("config/detekt/detekt.yml")
    val report = layout.buildDirectory.file("reports/detekt/detekt.sarif")
    inputs.files(detektSources).withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.file(configFile).withPathSensitivity(PathSensitivity.RELATIVE)
    outputs.file(report)
    classpath = files(detektCli)
    mainClass.set("io.gitlab.arturbosch.detekt.cli.Main")
    args(
        "--input",
        detektSources.joinToString(",") { it.absolutePath },
        "--config",
        configFile.absolutePath,
        "--build-upon-default-config",
        "--jvm-target",
        libs.versions.jvm.target
            .get(),
        "--report",
        "sarif:${report.get().asFile.absolutePath}",
    )
}
