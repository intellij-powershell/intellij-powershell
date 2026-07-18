// SPDX-FileCopyrightText: 2017 Andrey Dernov <https://github.com/ant-druha/>
// SPDX-FileCopyrightText: 2024 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.psi.impl.mixin

import com.intellij.lang.ASTNode
import com.intellij.plugin.powershell.psi.PowerShellArrayTypeElement
import com.intellij.plugin.powershell.psi.impl.PowerShellTypeElementImplGen
import com.intellij.plugin.powershell.psi.types.PowerShellType
import com.intellij.plugin.powershell.psi.types.impl.PowerShellArrayClassTypeImpl

abstract class PowerShellArrayTypeElementMixin(
  node: ASTNode?
) : PowerShellTypeElementImplGen(node), PowerShellArrayTypeElement {

  override fun getType(): PowerShellType {
    return PowerShellArrayClassTypeImpl(this)
  }
}
