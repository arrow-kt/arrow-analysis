
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.dokka) apply false
  alias(libs.plugins.arrowGradleConfig.nexus)
  alias(libs.plugins.arrowGradleConfig.formatter)
  alias(libs.plugins.arrowGradleConfig.versioning)
  java
}

allprojects {
  repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
  }

  group = property("projects.group").toString()
}

allprojects {
  this.tasks.withType<Test>() {
    useJUnitPlatform()
    testLogging {
      showStandardStreams = true
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
      events("passed", "skipped", "failed", "standardOut", "standardError")
    }

    systemProperty(
      "arrow.meta.generate.source.dir",
      File("$buildDir/generated/meta/tests").absolutePath
    )
    systemProperty("CURRENT_VERSION", "$version")
    systemProperty("arrowVersion", libs.versions.arrow.get())
    systemProperty("jvmTargetVersion", properties["jvmTargetVersion"].toString())
    jvmArgs = listOf("""-Dkotlin.compiler.execution.strategy="in-process"""")
  }
}

val toolchain = project.extensions.getByType<JavaToolchainService>()
allprojects {
  tasks.withType<JavaCompile>().configureEach {
    javaCompiler.set(toolchain.compilerFor {
      val jvmTargetVersion = properties["jvmTargetVersion"].toString()
      val javaVersion = if (jvmTargetVersion == "1.8") "8" else jvmTargetVersion
      languageVersion.set(JavaLanguageVersion.of(javaVersion))
    })
  }
}
