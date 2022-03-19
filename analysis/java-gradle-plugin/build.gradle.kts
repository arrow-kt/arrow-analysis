@file:Suppress("DSL_SCOPE_VIOLATION")

import io.gitlab.arturbosch.detekt.Detekt

plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  `java-gradle-plugin`
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
  alias(libs.plugins.arrowGradleConfig.versioning)
  alias(libs.plugins.kotlin.binaryCompatibilityValidator)
  alias(libs.plugins.detekt)
}

tasks.processResources {
  duplicatesStrategy = DuplicatesStrategy.WARN
  filesMatching("**/analysis.plugin.properties") {
    filter { it.replace("%analysisPluginVersion%", "$version") }
  }
}

dependencies {
  api(libs.arrowGradlePluginCommons)
  runtimeOnly(libs.classgraph)

  // Necessary during plugin execution to be found and added for compilation
  api(libs.arrowMeta)
  api(projects.arrowAnalysisJavaPlugin)
}

gradlePlugin {
  plugins {
    create("arrow") {
      id = "io.arrow-kt.analysis.java"
      displayName = "Arrow Analysis Java Gradle Plugin"
      implementationClass = "arrow.meta.plugin.gradle.AnalysisJavaGradlePlugin"
    }
  }
}

pluginBundle {
  website = "https://arrow-kt.io/docs/meta"
  vcsUrl = "https://github.com/arrow-kt/arrow-meta"
  description = "Functional companion to Kotlin's Compiler"
  tags = listOf("kotlin", "compiler", "arrow", "plugin", "meta")
}

tasks.test {
  systemProperty("arrow.meta.generate.source.dir", project.buildDir.absolutePath)
}

detekt {
  buildUponDefaultConfig = true
  allRules = true
}

tasks.withType<Detekt>().configureEach {
  reports {
    html.required.set(true)
    sarif.required.set(true)
    txt.required.set(false)
    xml.required.set(false)
  }
}
