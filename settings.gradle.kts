enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
}

dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("gradle/projects.libs.versions.toml"))
      val kotlinVersion: String? by settings
      kotlinVersion?.let { version("kotlin", it) }
    }
  }
}

rootProject.name = "arrow-analysis"

// Libraries

include(":arrow-meta")
project(":arrow-meta").projectDir = File("libs/arrow-meta")

include(":arrow-meta-test")
project(":arrow-meta-test").projectDir = File("libs/meta-test")

include(":arrow-gradle-plugin-commons")
project(":arrow-gradle-plugin-commons").projectDir = File("libs/gradle-plugin-commons")

//Plugins

// Analysis

include(":arrow-analysis-types")
project(":arrow-analysis-types").projectDir = File("analysis/types")

include(":arrow-analysis-common")
project(":arrow-analysis-common").projectDir = File("analysis/common")

include(":arrow-analysis-kotlin-plugin")
project(":arrow-analysis-kotlin-plugin").projectDir = File("analysis/kotlin-plugin")

include(":arrow-analysis-java-plugin")
project(":arrow-analysis-java-plugin").projectDir = File("analysis/java-plugin")

include(":arrow-analysis-kotlin-gradle-plugin")
project(":arrow-analysis-kotlin-gradle-plugin").projectDir = File("analysis/kotlin-gradle-plugin")

include(":arrow-analysis-java-gradle-plugin")
project(":arrow-analysis-java-gradle-plugin").projectDir = File("analysis/java-gradle-plugin")

include(":arrow-analysis-laws")
project(":arrow-analysis-laws").projectDir = File("analysis/laws")

include(":arrow-analysis-example")
project(":arrow-analysis-example").projectDir = File("analysis/example")

include(":arrow-analysis-java-example")
project(":arrow-analysis-java-example").projectDir = File("analysis/java-example")

//includeBuild("arrow-analysis-sample")
//includeBuild("arrow-proofs-example")
