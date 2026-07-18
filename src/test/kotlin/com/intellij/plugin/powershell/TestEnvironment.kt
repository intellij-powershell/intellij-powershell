// SPDX-FileCopyrightText: 2023-2024 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell

val isOnCiServer =
  System.getenv("CI").equals("true", ignoreCase = true)
