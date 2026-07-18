// SPDX-FileCopyrightText: 2017 Andrey Dernov <https://github.com/ant-druha/>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.psi.types

interface PowerShellType {

  companion object {
    val UNKNOWN = object : PowerShellType {
      override fun <T> accept(visitor: PowerShellTypeVisitor<T>): T? {
        return visitor.visitType(this)
      }

      override fun getName(): String {
        return "<Unknown>"
      }
    }
  }

  fun <T> accept(visitor: PowerShellTypeVisitor<T>): T?


  fun getName(): String
}