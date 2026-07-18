/*
 * SPDX-FileCopyrightText: 2017-2022 Andrey Dernov <https://github.com/ant-druha/>
 * SPDX-FileCopyrightText: 2023-2026 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease
import org.jetbrains.intellij.platform.gradle.tasks.GenerateLexerTask
import org.jetbrains.intellij.platform.gradle.tasks.GenerateParserTask
import org.jetbrains.intellij.platform.gradle.tasks.PrepareSandboxTask
import org.jetbrains.intellij.platform.gradle.tasks.VerifyPluginTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.security.MessageDigest
import java.util.zip.ZipFile

plugins {
  id("java")
  alias(libs.plugins.changelog)
  alias(libs.plugins.gradleJvmWrapper)
  alias(libs.plugins.intellijGrammarKit)
  alias(libs.plugins.intellijPlatform)
  alias(libs.plugins.kotlin)
}

val generatedParserSourceBase = layout.buildDirectory.dir("generated/parser")
val generatedLexerSourceBase = layout.buildDirectory.dir("generated/lexer")

sourceSets {
  main {
    java.srcDir(generatedParserSourceBase)
    java.srcDir(generatedLexerSourceBase)
  }
}

val pluginVersion: String by ext.properties
group = "com.intellij.plugin"
version = pluginVersion

repositories {
  intellijPlatform {
    defaultRepositories()
  }

  mavenCentral()
  ivy {
    url = uri("https://github.com/PowerShell/PSScriptAnalyzer/releases/download/")
    patternLayout { artifact("[revision]/[module].[revision].[ext]") }
    content { includeGroup("PSScriptAnalyzer") }
    metadataSources { artifact() }
  }
  ivy {
    url = uri("https://github.com/PowerShell/PowerShellEditorServices/releases/download/")
    patternLayout { artifact("v[revision]/[module].[ext]") }
    content { includeGroup("PowerShellEditorServices") }
    metadataSources { artifact() }
  }
}

val psScriptAnalyzerVersion = project.property("psScriptAnalyzerVersion") as String
val psScriptAnalyzerSha256Hash = project.property("psScriptAnalyzerSha256Hash") as String
val psScriptAnalyzer = configurations.create("psScriptAnalyzer")

val psesVersion = project.property("psesVersion") as String
val psesSha256Hash = project.property("psesSha256Hash") as String
val powerShellEditorServices = configurations.create("powerShellEditorServices")

dependencies {
  intellijPlatform {
    intellijIdea(libs.versions.intellij) {
      useInstaller = !libs.versions.intellij.get().contains("SNAPSHOT")
    }
    bundledPlugins("org.jetbrains.plugins.terminal")
    bundledModule("intellij.platform.langInjection")
    testFramework(TestFrameworkType.Platform)
    testFramework(TestFrameworkType.JUnit5)
    testFramework(TestFrameworkType.Plugin.Debugger)
    pluginVerifier()
  }

  implementation(libs.lsp4j)
  implementation(libs.lsp4jdebug)
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
  testImplementation(libs.junit.jupiter.api)
  testImplementation(libs.openTest4J)
  testRuntimeOnly(libs.junit.jupiter.engine)
  testRuntimeOnly(libs.junit.platform.engine)
  testRuntimeOnly(libs.junit.platform.launcher)
  testRuntimeOnly(libs.junit.vintage.engine)

  psScriptAnalyzer(
    "PSScriptAnalyzer:PSScriptAnalyzer:$psScriptAnalyzerVersion@nupkg"
  )

  powerShellEditorServices(
    "PowerShellEditorServices:PowerShellEditorServices:$psesVersion@zip"
  )
}

intellijPlatform {
  pluginConfiguration {
    name = "PowerShell"
  }
  pluginVerification {
    ides {
      select {
        channels = listOf(
          ProductRelease.Channel.RELEASE,
          ProductRelease.Channel.EAP
        )
        untilBuild = providers.gradleProperty("untilBuildForVerification")
      }
    }
    freeArgs.addAll(
      "-mute", "ForbiddenPluginIdPrefix",
      "-mute", "TemplateWordInPluginId"
    )
    failureLevel.add(VerifyPluginTask.FailureLevel.INTERNAL_API_USAGES)
  }
}

configurations {
  runtimeClasspath {
    // NOTE: IntelliJ provides newer versions of these libraries, so let's exclude them from the dependency set of
    // org.eclipse.lsp4j:
    exclude("com.google.code.gson", "gson")
    exclude("com.google.guava", "guava")
  }
}

tasks {
  val generateLexer = named<GenerateLexerTask>("generateLexer") {
    purgeOldFiles = true
    sourceFile = file("src/main/flex/_PowerShellLexer.flex")
    targetRootOutputDir = generatedLexerSourceBase
    outputs.dir(targetRootOutputDir)
    pathToClass = "com/intellij/plugin/powershell/lang/_PowerShellLexer.java"
    defaultCharacterEncoding = "UTF-8"
  }

  val generateParser = named<GenerateParserTask>("generateParser") {
    purgeOldFiles = true
    sourceFile = file("src/main/bnf/PowerShell.bnf")
    targetRootOutputDir = generatedParserSourceBase
    outputs.dir(targetRootOutputDir)
    defaultCharacterEncoding = "UTF-8"
  }

  withType<Test> {
    useJUnitPlatform()
  }

  withType<JavaCompile> {
    dependsOn(generateLexer, generateParser)

    options.apply {
      compilerArgs.add("-Werror")
      encoding = "UTF-8"
    }
    sourceCompatibility = "21"
    targetCompatibility = "21"
  }
  withType<KotlinCompile> {
    dependsOn(generateLexer, generateParser)

    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_21)
      allWarningsAsErrors = true
    }
  }

  fun File.verifyHash(expectedHash: String) {
    println("Calculating hash for $name...")
    val data = readBytes()
    val hash = MessageDigest.getInstance("SHA-256").let { sha256 ->
      sha256.update(data)
      sha256.digest().joinToString("") { "%02X".format(it) }
    }
    println("Expected hash for $name = $expectedHash")
    println("Calculated hash for $name = $hash")
    if (!hash.equals(expectedHash, ignoreCase = true)) {
      error("$name hash check failed.\n" +
        "Please try re-downloading the dependency, or update the expected hash in the gradle.properties file.")
    }
  }

  fun PrepareSandboxTask.unpackPsScriptAnalyzer(outDir: String) {
    inputs.files(psScriptAnalyzer)
    inputs.property("hash", psScriptAnalyzerSha256Hash)

    doFirst {
      psScriptAnalyzer.singleFile.verifyHash(psScriptAnalyzerSha256Hash)
    }

    from(zipTree(psScriptAnalyzer.singleFile)) {
      into("$outDir/PSScriptAnalyzer")

      // NuGet stuff:
      exclude("_manifest/**", "_rels/**", "package/**", "[Content_Types].xml", "*.nuspec")

      // Compatibility profiles, see https://github.com/PowerShell/PSScriptAnalyzer/issues/1148
      exclude("compatibility_profiles/**")
    }
  }

  fun PrepareSandboxTask.unpackPowerShellEditorServices(outDir: String) {
    inputs.files(powerShellEditorServices)
    inputs.property("hash", psesSha256Hash)
    doFirst {
      powerShellEditorServices.singleFile.verifyHash(psesSha256Hash)
    }

    from(zipTree(powerShellEditorServices.singleFile)) {
      into(outDir)
      // We only need this module and not anything else from the archive:
      include("PowerShellEditorServices/**")
    }
  }

  withType<PrepareSandboxTask> {
    val outDir = "${project.name}/lib/LanguageHost/modules"
    unpackPsScriptAnalyzer(outDir)
    unpackPowerShellEditorServices(outDir)
  }

  val maxUnpackedPluginBytes = project.property("maxUnpackedPluginBytes") as String
  val verifyDistributionSize = register("verifyDistributionSize") {
    description = "Verify that the resulting plugin artifact size is not unexpectedly large."
    group = "verification"

    val pluginArtifact = buildPlugin.flatMap { it.archiveFile }

    inputs.file(pluginArtifact)
    inputs.property("maxUnpackedPluginBytes", maxUnpackedPluginBytes)
    outputs.upToDateWhen { true }

    doLast {
      val artifact = pluginArtifact.get().asFile
      val unpackedSize = ZipFile(artifact).use { it.entries().asSequence().sumOf { e -> e.size } }
      val unpackedSizeMiB = "%.3f".format(unpackedSize / 1024.0 / 1024.0)
      if (unpackedSize > maxUnpackedPluginBytes.toLong()) {
        error(
          "The resulting artifact size is too large. Expected no more than $maxUnpackedPluginBytes, but got" +
            " $unpackedSize bytes ($unpackedSizeMiB MiB).\nArtifact path: \"$artifact\"."
        )
      }

      println("Verified unpacked distribution size: $unpackedSizeMiB MiB.")
    }
  }

  check {
    dependsOn(verifyDistributionSize)
    dependsOn(verifyPlugin)
  }

  runIde {
    jvmArgs("-Dide.plugins.snapshot.on.unload.fail=true", "-XX:+UnlockDiagnosticVMOptions")
    autoReload = true
  }

  patchPluginXml {
    changeNotes.set(provider {
      changelog.renderItem(
        changelog
          .getLatest()
          .withHeader(false)
          .withEmptySections(false),
        org.jetbrains.changelog.Changelog.OutputType.HTML
      )
    })
  }
}
