// SPDX-FileCopyrightText: 2018 Andrey Dernov <https://github.com/ant-druha/>
// SPDX-FileCopyrightText: 2023-2024 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.lang.lsp.languagehost

import java.io.InputStream
import java.io.OutputStream

interface LanguageHostConnectionManager {
  suspend fun establishConnection(): Pair<InputStream, OutputStream>?
  suspend fun establishDebuggerConnection(): Pair<InputStream, OutputStream>?
  fun closeConnection()
  fun isConnected(): Boolean
  fun getProcess(): Process?
  fun createProcess(command: List<String>, environment: Map<String, String>?): Process
  fun connectServer(server: LanguageServerEndpoint) {}
  fun useConsoleRepl(): Boolean = false
}
