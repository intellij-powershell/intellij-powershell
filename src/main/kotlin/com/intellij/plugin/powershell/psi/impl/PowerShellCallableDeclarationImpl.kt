// SPDX-FileCopyrightText: 2017 Andrey Dernov <https://github.com/ant-druha/>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.psi.impl

import com.intellij.lang.ASTNode
import com.intellij.plugin.powershell.psi.PowerShellCallableDeclaration
import com.intellij.plugin.powershell.psi.types.PowerShellType
import com.intellij.plugin.powershell.psi.types.impl.PowerShellObjectType

open class PowerShellCallableDeclarationImpl(node: ASTNode) : PowerShellAbstractComponent(node), PowerShellCallableDeclaration {
  override fun getReturnType(): PowerShellType? {
    return PowerShellObjectType()//stub
  }
}