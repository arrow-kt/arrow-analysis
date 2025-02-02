@file:Suppress("DSL_SCOPE_VIOLATION")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
  alias(libs.plugins.arrowGradleConfig.versioning)
  alias(libs.plugins.kotlin.binaryCompatibilityValidator)
}

kotlin {
  explicitApi = null
  jvmToolchain {
    (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11))
  }
}

sourceSets {
  main {
    java.setSrcDirs(listOf("src/main/java"))
    resources.setSrcDirs(listOf("resources"))
  }
  test {
    java.setSrcDirs(listOf("src/test", "src/test-gen"))
    resources.setSrcDirs(listOf("testResources"))
  }
}

dependencies {

  implementation(libs.arrowMeta)
  implementation(projects.arrowAnalysisCommon)

  testImplementation(libs.ksp.api)
  testImplementation(libs.ksp.lib)
  testImplementation(libs.arrowMetaTest)
  testImplementation(projects.arrowAnalysisLaws)

  val kotlinVersion: String = libs.versions.kotlin.get()

  println("Kotlin version: $kotlinVersion")

  "org.jetbrains.kotlin:kotlin-compiler:$kotlinVersion".let {
    compileOnly(it)
    testImplementation(it)
  }

  testRuntimeOnly("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
  testRuntimeOnly("org.jetbrains.kotlin:kotlin-script-runtime:$kotlinVersion")
  testRuntimeOnly("org.jetbrains.kotlin:kotlin-annotations-jvm:$kotlinVersion")

  testImplementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
  testImplementation("org.jetbrains.kotlin:kotlin-compiler-internal-test-framework:$kotlinVersion")
  testImplementation("junit:junit:4.13.2")

  testImplementation(platform("org.junit:junit-bom:5.8.0"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation("org.junit.platform:junit-platform-commons")
  testImplementation("org.junit.platform:junit-platform-launcher")
  testImplementation("org.junit.platform:junit-platform-runner")
  testImplementation("org.junit.platform:junit-platform-suite-api")

}

tasks.test {
  dependsOn(project(":arrow-analysis-types").tasks.getByName("jvmJar"))
  dependsOn(project(":arrow-analysis-laws").tasks.getByName("jvmJar"))
  useJUnitPlatform()
  doFirst {
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib", "kotlin-stdlib")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib-jdk8", "kotlin-stdlib-jdk8")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-reflect", "kotlin-reflect")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-test", "kotlin-test")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-script-runtime", "kotlin-script-runtime")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-annotations-jvm", "kotlin-annotations-jvm")
  }
}

tasks.withType<KotlinCompile>().configureEach {
  compilerOptions {
    optIn.add("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
    optIn.add("org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI")
  }
}

val generateTests by tasks.registering(JavaExec::class) {
  classpath = sourceSets.test.get().runtimeClasspath
  mainClass.set("arrow.meta.plugins.analysis.GenerateTestsKt")
}

val compileTestKotlin by tasks.getting {
  doLast {
    generateTests.get().exec()
  }
}

fun Test.setLibraryProperty(propName: String, jarName: String) {
  val path = project.configurations
    .testRuntimeClasspath.get()
    .files
    .find { """$jarName-\d.*jar""".toRegex().matches(it.name) }
    ?.absolutePath
    ?: return
  systemProperty(propName, path)
}
