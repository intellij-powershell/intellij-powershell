// SPDX-FileCopyrightText: 2017 Andrey Dernov <https://github.com/ant-druha/>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.psi.types.impl

import com.intellij.plugin.powershell.psi.types.PowerShellType
import com.intellij.plugin.powershell.psi.types.PowerShellTypeVisitor

class PowerShellObjectType: PowerShellType {
  override fun <T> accept(visitor: PowerShellTypeVisitor<T>): T? {
    return visitor.visitType(this)
  }

  override fun getName(): String = "Object"
}