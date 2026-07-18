// SPDX-FileCopyrightText: 2017 Guillaume Tâche
// SPDX-FileCopyrightText: 2017-2018 intellij-lsp contributors <https://github.com/gtache/intellij-lsp>
// SPDX-FileCopyrightText: 2018 Andrey Dernov <https://github.com/ant-druha/>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.lang.lsp.ide.listeners

import com.intellij.openapi.diagnostic.Logger
import com.intellij.plugin.powershell.lang.lsp.ide.EditorEventManager

open class LSPEditorListener {
  protected var editorManager: EditorEventManager? = null
  private val LOG: Logger = Logger.getInstance(javaClass)

  fun setManager(manager: EditorEventManager) {
    this.editorManager = manager
  }

  protected fun checkManager(): Boolean {
    return if (editorManager == null) {
      LOG.error("Manager is null")
      false
    } else true
  }
}
