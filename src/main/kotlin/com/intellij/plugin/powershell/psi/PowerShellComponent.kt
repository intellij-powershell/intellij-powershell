// SPDX-FileCopyrightText: 2017 Andrey Dernov <https://github.com/ant-druha/>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.psi

import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.PsiNamedElement

/**
 * Andrey 15/08/17.
 */
interface PowerShellComponent : PowerShellPsiElement, PsiNamedElement, PsiNameIdentifierOwner {
  override fun getName(): String?
  override fun getNameIdentifier(): PowerShellIdentifier?
}