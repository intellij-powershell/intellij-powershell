// SPDX-FileCopyrightText: 2017 Andrey Dernov <https://github.com/ant-druha/>
// SPDX-FileCopyrightText: 2024 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.psi.types.impl

import com.intellij.plugin.powershell.psi.PowerShellComponent
import com.intellij.plugin.powershell.psi.PowerShellTypeDeclaration
import com.intellij.plugin.powershell.psi.types.PowerShellImmediateClassType
import com.intellij.plugin.powershell.psi.types.PowerShellTypeVisitor

class PowerShellImmediateClassTypeImpl(private val myClass: PowerShellTypeDeclaration) : PowerShellImmediateClassType {
  override fun <T> accept(visitor: PowerShellTypeVisitor<T>): T? {
    return visitor.visitClassType(this)
  }

  override fun resolve(): PowerShellComponent {
    return myClass
  }

  override fun getName(): String {
    return myClass.name ?: "<Unnamed>"
  }
}
