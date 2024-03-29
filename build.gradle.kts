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

tasks {
  create<Exec>("generateDoc") {
    commandLine("sh", "gradlew", "dokkaJekyll")
  }

  create("buildDoc") {
    group = "documentation"
    description = "Generates API Doc and validates all the documentation"
    dependsOn("generateDoc")
  }
}

allprojects {
  extra.set("dokka.outputDirectory", rootDir.resolve("docs/docs/apidocs"))
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
    systemProperty("jvmTargetVersion", "11")
    jvmArgs = listOf(
      """-Dkotlin.compiler.execution.strategy="in-process"""",
      "--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
      "--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
      "--add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
      "--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
      "--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
      "--add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
      "--add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED"
    )
  }
}

val projects = listOf("common", "laws", "types", "kotlin-plugin", "kotlin-gradle-plugin", "java-plugin", "java-gradle-plugin")
projects.forEach { thing ->
  projects.forEach { dep ->
    tasks.getByPath(":arrow-analysis-$thing:docsJar").dependsOn(":arrow-analysis-$dep:dokkaHtml")
  }
}
