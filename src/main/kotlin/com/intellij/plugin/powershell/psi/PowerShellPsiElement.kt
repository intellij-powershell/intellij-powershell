// SPDX-FileCopyrightText: 2017 Andrey Dernov <https://github.com/ant-druha/>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.psi

import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement

/**
 * Andrey 26/06/17.
 */
interface PowerShellPsiElement : NavigatablePsiElement {
  override fun getPresentation(): ItemPresentation
}