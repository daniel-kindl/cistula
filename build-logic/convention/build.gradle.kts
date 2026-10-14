plugins {
    `kotlin-dsl`
}

dependencies {
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
