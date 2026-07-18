// SPDX-FileCopyrightText: 2017-2018 Andrey Dernov <https://github.com/ant-druha/>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.lang.parser

import com.intellij.plugin.powershell.lang.PowerShellLanguage
import com.intellij.psi.tree.IElementType

/**
 * Andrey 26/06/17.
 */
class PowerShellElementType(debugName: String) : IElementType(debugName, PowerShellLanguage.INSTANCE)

