/*
 * SPDX-FileCopyrightText: 2026 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.intellij.plugin.powershell.ide

import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.openapi.util.io.toNioPathOrNull
import java.nio.file.Path
import kotlin.io.path.exists

fun findExecutableInPath(pathOrFileNameOrBaseName: String): Path? {
  // Absolute path: just resolve and return if exists.
  val path = pathOrFileNameOrBaseName.toNioPathOrNull() ?: return null
  if (path.isAbsolute) {
    return if (path.exists()) path else null
  }

  // Only a bare file name or base name may be looked up in PATH.
  if (pathOrFileNameOrBaseName.isBlank() || pathOrFileNameOrBaseName.any { it == '/' || it == '\\' }) {
    return null
  }

  // Finds either an exact file name, or (on Windows) a base name with any of the executable extensions.
  @Suppress("UnstableApiUsage")
  return PathEnvironmentVariableUtil.findFirst(pathOrFileNameOrBaseName)
}
