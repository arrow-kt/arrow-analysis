@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.formatter)
}


kotlin {
  explicitApi = null

  jvmToolchain {
    (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11))
  }

  sourceSets {
    commonMain {
      dependencies {
        implementation(libs.kotlin.stdlibCommon)
        api(projects.arrowAnalysisLaws)
        api(projects.arrowAnalysisTypes)
      }
    }

    named("jvmMain") {
      dependencies {
        implementation(libs.kotlin.stdlibJDK8)
      }
    }

    named("jsMain") {
      dependencies {
        implementation(libs.kotlin.stdlibJS)
      }
    }
  }
}

dependencies {
  kotlinCompilerClasspath(projects.arrowAnalysisKotlinPlugin)
}

tasks.compileKotlinJvm {
  kotlinOptions {
    dependsOn(":arrow-analysis-laws:jar")
    freeCompilerArgs = listOf(
      "-Xplugin=$rootDir/analysis/kotlin-plugin/build/libs/arrow-analysis-kotlin-plugin-$version.jar",
      "-P", "plugin:arrow.meta.plugin.compiler.analysis:generatedSrcOutputDir=$buildDir/generated/meta",
      "-P", "plugin:arrow.meta.plugin.compiler.analysis:baseDir=${project.rootProject.rootDir.path}"
    )
  }
}

// disable publication
tasks.withType<PublishToMavenLocal>().configureEach { enabled = false }
tasks.withType<PublishToMavenRepository>().configureEach { enabled = false }
