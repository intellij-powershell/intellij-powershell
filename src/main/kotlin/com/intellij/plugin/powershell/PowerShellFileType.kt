// SPDX-FileCopyrightText: 2017-2019 Andrey Dernov <https://github.com/ant-druha/>
// SPDX-FileCopyrightText: 2024 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.plugin.powershell.lang.PowerShellLanguage
import javax.swing.Icon

/**
 * Andrey 17/07/17.
 */
class PowerShellFileType : LanguageFileType(PowerShellLanguage.INSTANCE) {

  companion object {
    @JvmStatic
    val INSTANCE = PowerShellFileType()
  }

  override fun getIcon(): Icon {
    return PowerShellIcons.FILE
  }

  override fun getName(): String {
    return "PowerShell"
  }

  override fun getDefaultExtension(): String {
    return "ps1"
  }

  override fun getDescription(): String {
    return "PowerShell file"
  }
}
