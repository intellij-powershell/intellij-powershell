// SPDX-FileCopyrightText: 2017 Andrey Dernov <https://github.com/ant-druha/>
// SPDX-FileCopyrightText: 2024 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.ide.resolve

import com.intellij.plugin.powershell.psi.PowerShellReferencePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.util.PsiTreeUtil

class PowerShellComponentResolveProcessor : ResolveCache.AbstractResolver<PowerShellReferencePsiElement, List<PsiElement>> {

  companion object {
    val INSTANCE = PowerShellComponentResolveProcessor()
  }

  override fun resolve(ref: PowerShellReferencePsiElement, incompleteCode: Boolean): List<PsiElement> {

    // local
    val resolveProcessor = PowerShellComponentScopeProcessor()
    PsiTreeUtil.treeWalkUp(resolveProcessor, ref.element, null, ResolveState.initial())
    val results = resolveProcessor.getResult()
    return results.toList()
  }
}
