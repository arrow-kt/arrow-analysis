@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  `java-gradle-plugin`
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.formatter)
  alias(libs.plugins.arrowGradleConfig.publish)
  alias(libs.plugins.kotlin.binaryCompatibilityValidator)
}

tasks.processResources {
  duplicatesStrategy = DuplicatesStrategy.WARN
  filesMatching("**/analysis.plugin.properties") {
    filter { it.replace("%analysisPluginVersion%", "$version") }
  }
}

kotlin {
  explicitApi = null
  jvmToolchain {
    (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11))
  }
}

dependencies {
  api(libs.arrowGradlePluginCommons)
  runtimeOnly(libs.classgraph)

  // Necessary during plugin execution to be found and added for compilation
  api(libs.arrowMeta)
  api(projects.arrowAnalysisKotlinPlugin)
}

gradlePlugin {
  plugins {
    create("arrow") {
      id = "io.arrow-kt.analysis.kotlin"
      displayName = "Arrow Analysis Kotlin Gradle Plugin"
      implementationClass = "arrow.meta.plugin.gradle.AnalysisGradlePlugin"
    }
  }
}

pluginBundle {
  website = "https://arrow-kt.io/docs/meta"
  vcsUrl = "https://github.com/arrow-kt/arrow-meta"
  description = "Functional companion to Kotlin's Compiler"
  tags = listOf("kotlin", "compiler", "arrow", "plugin", "meta")
}
