// SPDX-FileCopyrightText: 2017-2018 Andrey Dernov <https://github.com/ant-druha/>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.lang.lexer

import com.intellij.lexer.FlexAdapter
import com.intellij.plugin.powershell.lang._PowerShellLexer

/**
 * Andrey 17/07/17.
 */
class PowerShellLexerAdapter : FlexAdapter(_PowerShellLexer()) 