// SPDX-FileCopyrightText: 2026 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.ide.run

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.DumbAware
import com.intellij.plugin.powershell.psi.impl.PowerShellFile
import com.intellij.psi.PsiElement

class PowerShellRunLineMarkerContributor : RunLineMarkerContributor(), DumbAware {
  override fun getInfo(element: PsiElement): Info? {
    if (element.textOffset != 0) return null

    val file = element.containingFile as? PowerShellFile ?: return null
    if (!file.virtualFile?.extension.equals("ps1", ignoreCase = true)) return null
    if (element !== file.findElementAt(0)) return null

    return withExecutorActions(AllIcons.RunConfigurations.TestState.Run)
  }
}
