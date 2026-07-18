// SPDX-FileCopyrightText: 2017 Andrey Dernov <https://github.com/ant-druha/>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.psi

import com.intellij.plugin.powershell.psi.types.PowerShellType

interface PowerShellCallableDeclaration : PowerShellComponent {
  fun getReturnType(): PowerShellType?
}