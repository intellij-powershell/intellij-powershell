// SPDX-FileCopyrightText: 2017-2018 Andrey Dernov <https://github.com/ant-druha/>
// SPDX-FileCopyrightText: 2023-2024 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.lang

import com.intellij.lang.Language

class PowerShellLanguage : Language("PowerShell") {

  companion object {
    @JvmStatic
    val INSTANCE = PowerShellLanguage()

    /**
     * Language id for PowerShell LSP.
     */
    const val LSP_ID = "powershell"
  }
}
