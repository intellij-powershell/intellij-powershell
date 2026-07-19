/*
 * SPDX-FileCopyrightText: 2023-2026 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.intellij.plugin.powershell.lang

import com.intellij.openapi.util.SystemInfo
import com.intellij.plugin.powershell.ide.findExecutableInPath
import com.intellij.plugin.powershell.isOnCiServer
import com.intellij.plugin.powershell.lang.lsp.languagehost.PSLanguageHostUtils
import com.intellij.plugin.powershell.lang.lsp.languagehost.PSVersionInfo
import com.intellij.plugin.powershell.lang.lsp.languagehost.PowerShellEdition
import com.intellij.testFramework.junit5.TestApplication
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.pathString
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

@TestApplication
class PSLanguageHostUtilsTests {

  @Test
  fun testNormalizePSExtensionPath(@TempDir tempDir: Path) {
    val extensionRoot = (tempDir / "extension").createDirectories()
    val moduleBase = (extensionRoot / "modules").createDirectories()
    val moduleDirectory = (moduleBase / "PowerShellEditorServices").createDirectories()
    val manifest = (moduleDirectory / "PowerShellEditorServices.psd1").createFile()
    val startupScript = (moduleDirectory / "Start-EditorServices.ps1").createFile()
    val directChild = (moduleDirectory / "Commands.ps1").createFile()

    assertEquals(extensionRoot.pathString, PSLanguageHostUtils.normalizePSExtensionPath(extensionRoot.pathString))
    assertEquals(moduleBase.pathString, PSLanguageHostUtils.normalizePSExtensionPath(moduleBase.pathString))
    assertEquals(moduleBase.pathString, PSLanguageHostUtils.normalizePSExtensionPath(moduleDirectory.pathString))
    assertEquals(moduleBase.pathString, PSLanguageHostUtils.normalizePSExtensionPath(manifest.pathString))
    assertEquals(moduleBase.pathString, PSLanguageHostUtils.normalizePSExtensionPath(directChild.pathString))
    assertEquals(moduleBase.pathString, PSLanguageHostUtils.getPSExtensionModulesDir(manifest.pathString))
    assertEquals(startupScript.pathString, PSLanguageHostUtils.getEditorServicesStartupScript(directChild.pathString))
  }

  @Test
  fun testNormalizePSExtensionPathLeavesInvalidBoundariesUnchanged(@TempDir tempDir: Path) {
    val missing = tempDir / "missing"
    val unrelatedFile = (tempDir / "unrelated.txt").createFile()
    val moduleDirectory = (tempDir / "PowerShellEditorServices").createDirectories()
    (moduleDirectory / "PowerShellEditorServices.psd1").createFile()
    val nestedDirectory = (moduleDirectory / "nested").createDirectories()
    val nestedFile = (nestedDirectory / "nested.ps1").createFile()

    listOf("", " ", missing.pathString, unrelatedFile.pathString, nestedDirectory.pathString, nestedFile.pathString).forEach {
      assertEquals(it, PSLanguageHostUtils.normalizePSExtensionPath(it))
    }
  }

  @Test
  fun testBundledEditorServicesModulesArePresent() {
    val bundledPath = Path(PSLanguageHostUtils.BUNDLED_PSES_PATH)
    val editorServicesModuleDir = bundledPath / "modules" / "PowerShellEditorServices"
    assertTrue(
      editorServicesModuleDir.exists(),
      "Expected bundled EditorServices module directory at ${editorServicesModuleDir.pathString}."
    )

    val startupScriptPath = Path(PSLanguageHostUtils.getEditorServicesStartupScript(bundledPath.pathString))
    assertTrue(
      startupScriptPath.exists(),
      "Expected bundled EditorServices startup script at ${startupScriptPath.pathString}."
    )
  }

  @Test
  fun testPowerShell5VersionDetector() {
    if (!SystemInfo.isWindows)
      return

    val executable = findExecutableInPath("powershell")
    if (executable == null) {
      if (isOnCiServer) {
        fail("powershell.exe not found on CI environment.")
        return
      } else {
        // Ignore the absence of an executable in the developer environment.
        return
      }
    }


    doTest(executable, "5\\..+".toRegex(), PowerShellEdition.Desktop)
  }

  @Test
  fun testPowerShellCoreVersionDetector() {
    val executable = findExecutableInPath("pwsh")
    if (executable == null) {
      if (isOnCiServer) {
        fail("PowerShell Core executable not found on CI environment.")
        return
      } else {
        // Ignore the absence of an executable in the developer environment.
        return
      }
    }

    doTest(executable, "[67]\\..+".toRegex(), PowerShellEdition.Core)
  }

  private fun doTest(executable: Path, expectedVersionRegex: Regex, expectedEdition: PowerShellEdition) {
    val version: PSVersionInfo = runBlocking { PSLanguageHostUtils.getPowerShellVersion(executable.pathString).await() }
    assertTrue(
      expectedVersionRegex.matches(version.versionString),
      "Version string ${version.versionString} is expected to satisfy a regular expression $expectedVersionRegex."
    )
    Assertions.assertEquals(expectedEdition, version.edition)
  }
}
