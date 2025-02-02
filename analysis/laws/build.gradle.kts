@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
  alias(libs.plugins.arrowGradleConfig.versioning)
  alias(libs.plugins.arrowGradleConfig.formatter)
  alias(libs.plugins.kotlin.binaryCompatibilityValidator)
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
        implementation(kotlin("stdlib-common"))
        api(projects.arrowAnalysisTypes)
      }
    }

    jvmMain {
      dependencies {
        implementation(libs.kotlin.stdlibJDK8)
        implementation(kotlin("stdlib-jdk8"))
      }
    }

    jsMain {
      dependencies {
        implementation(libs.kotlin.stdlibJS)
        implementation(kotlin("stdlib-js"))
      }
    }
  }
}

dependencies {
  kotlinCompilerPluginClasspath(projects.arrowAnalysisKotlinPlugin)
}

tasks.compileKotlinJvm {
  compilerOptions {
    dependsOn(":arrow-analysis-kotlin-plugin:jar")
    freeCompilerArgs.set(listOf(
      "-Xplugin=$rootDir/analysis/kotlin-plugin/build/libs/arrow-analysis-kotlin-plugin-$version.jar",
      "-P", "plugin:arrow.meta.plugin.compiler.analysis:generatedSrcOutputDir=$buildDir/generated/meta",
      "-P", "plugin:arrow.meta.plugin.compiler.analysis:baseDir=${project.rootProject.rootDir.path}"
    ))
  }
}

tasks.compileKotlinJs {
  kotlinOptions.suppressWarnings = true
}

tasks.compileKotlinMetadata {
  kotlinOptions.suppressWarnings = true
}

apiValidation {
  validationDisabled = true
}
