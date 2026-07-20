/*
 * SPDX-FileCopyrightText: 2018-2021 Andrey Dernov <https://github.com/ant-druha/>
 * SPDX-FileCopyrightText: 2023-2026 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.intellij.plugin.powershell.lang.lsp.languagehost

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.application.PluginPathManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.plugin.powershell.ide.PluginAppRoot
import com.intellij.plugin.powershell.ide.run.checkExists
import com.intellij.plugin.powershell.ide.run.getModuleVersion
import com.intellij.plugin.powershell.ide.run.join
import com.intellij.util.io.awaitExit
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import java.io.File
import java.io.InputStream
import java.util.concurrent.CompletableFuture

object PSLanguageHostUtils {
  private const val EDITOR_SERVICES_MANIFEST = "PowerShellEditorServices.psd1"

  val LOG: Logger = Logger.getInstance(javaClass)
  val BUNDLED_PSES_PATH by lazy {
    PluginPathManager.getPluginResource(PSLanguageHostUtils.javaClass, "lib/LanguageHost")?.path
      ?: error("Cannot find plugin resources folder.")
  }

  fun getPSExtensionModulesDir(psExtensionDir: String): String {
    val normalizedDir = normalizePSExtensionPath(psExtensionDir)
    return if (isExtensionDirectoryFormat(normalizedDir)) join(normalizedDir, "modules")
    else normalizedDir
  }

  fun getEditorServicesStartupScript(psExtensionDir: String): String {
    val normalizedDir = normalizePSExtensionPath(psExtensionDir)
    return when {
      isExtensionDirectoryFormat(normalizedDir) -> join(normalizedDir, "modules/PowerShellEditorServices/Start-EditorServices.ps1")
      isStandAloneDirectoryFormat(normalizedDir) -> File(File(normalizedDir, "PowerShellEditorServices"), "Start-EditorServices.ps1").path
      else -> join(BUNDLED_PSES_PATH, "modules/PowerShellEditorServices/Start-EditorServices.ps1")
    }
  }

  fun normalizePSExtensionPath(path: String): String {
    if (path.isBlank()) return path
    return runCatching {
      val selectedFile = File(path)
      val moduleDirectory = when {
        !selectedFile.exists() -> null
        selectedFile.isFile -> selectedFile.parentFile
        else -> selectedFile
      }
      if (moduleDirectory != null && File(moduleDirectory, EDITOR_SERVICES_MANIFEST).isFile) {
        moduleDirectory.parent ?: path
      } else path
    }.getOrDefault(path)
  }

  private fun isExtensionDirectoryFormat(psExtensionDir: String): Boolean {
    return checkExists("$psExtensionDir/modules")
  }

  private fun isStandAloneDirectoryFormat(psExtensionDir: String): Boolean {
    return checkExists("$psExtensionDir/PowerShellEditorServices/Start-EditorServices.ps1")
  }

  @Throws(PowerShellExtensionError::class)
  fun getEditorServicesModuleVersion(moduleBase: String): String {
    return getModuleVersion(moduleBase, "PowerShellEditorServices")
  }

  fun getPowerShellVersion(powerShellExePath: String): CompletableFuture<PSVersionInfo> {
    return PluginAppRoot.getInstance().coroutineScope.async(Dispatchers.IO) {
      readPowerShellVersion(powerShellExePath)
    }.asCompletableFuture()
  }
}

private suspend fun readPowerShellVersion(exePath: String): PSVersionInfo {
  var process: Process? = null
  val commandString = "(\$PSVersionTable.PSVersion, \$PSVersionTable.PSEdition) -join ' '"
  val commandLine = GeneralCommandLine(exePath, "–NoProfile", "-NonInteractive", "-Command", commandString)
  return coroutineScope {
    try {
      process = commandLine.createProcess()
      fun readStream(stream: InputStream) = async {
        runInterruptible { stream.reader().use { it.readText() } }
      }

      val stdOutReader = readStream(process.inputStream)
      val stdErrReader = readStream(process.errorStream)
      val exitCode = process.awaitExit()
      if (exitCode != 0) {
        val stdOut = stdOutReader.await()
        val stdErr = stdErrReader.await()
        val message = buildString {
          append("Process exit code $exitCode.")
          if (stdOut.isNotBlank()) {
            append("\nStandard output:\n$stdOut")
          }
          if (stdErr.isNotBlank()) {
            append("\nStandard error:\n$stdErr")
          }
        }
        error(message)
      }

      PSVersionInfo.parse(stdOutReader.await().trim())
    } catch (e: Exception) {
      if(e is CancellationException) throw e
      PSLanguageHostUtils.LOG.warn("Command execution failed for ${commandLine.preparedCommandLine}", e)
      throw PowerShellControlFlowException(e.message, e.cause)
    } finally {
      process?.destroy()
    }
  }
}
