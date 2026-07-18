// SPDX-FileCopyrightText: 2025 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.testFramework

import com.intellij.openapi.application.EDT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun runInEdt(test: suspend () -> Unit) {
  runBlocking {
    withContext(Dispatchers.EDT) {
      test()
    }
  }
}
