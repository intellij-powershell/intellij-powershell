// SPDX-FileCopyrightText: 2017 Andrey Dernov <https://github.com/ant-druha/>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.psi

import com.intellij.psi.PsiQualifiedReference

/**
 * Andrey 18/08/17.
 */
interface PowerShellQualifiedReferenceElement<out Q : PowerShellPsiElement> : PowerShellReferencePsiElement, PsiQualifiedReference {
  override fun getQualifier(): Q?
  override fun getReferenceName(): String?
  fun isTypeMemberAccess(): Boolean
}