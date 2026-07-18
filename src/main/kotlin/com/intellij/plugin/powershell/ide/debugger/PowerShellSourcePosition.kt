// SPDX-FileCopyrightText: 2024 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.ide.debugger

import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.XSourcePositionWrapper

class PowerShellSourcePosition(position: XSourcePosition) : XSourcePositionWrapper(position)
