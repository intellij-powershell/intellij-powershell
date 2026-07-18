// SPDX-FileCopyrightText: 2021 Andrey Dernov <https://github.com/ant-druha/>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.lang.lsp.languagehost

import com.intellij.openapi.diagnostic.ControlFlowException

class PowerShellControlFlowException(message: String?, throwable: Throwable?) : Throwable(message, throwable), ControlFlowException