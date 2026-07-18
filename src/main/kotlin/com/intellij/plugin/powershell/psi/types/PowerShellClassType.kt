// SPDX-FileCopyrightText: 2017 Andrey Dernov <https://github.com/ant-druha/>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.psi.types

import com.intellij.plugin.powershell.psi.PowerShellComponent

interface PowerShellClassType: PowerShellType {
  fun resolve(): PowerShellComponent?
}