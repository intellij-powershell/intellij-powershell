// SPDX-FileCopyrightText: 2017 Guillaume Tâche
// SPDX-FileCopyrightText: 2017-2018 intellij-lsp contributors <https://github.com/gtache/intellij-lsp>
// SPDX-FileCopyrightText: 2018 Andrey Dernov <https://github.com/ant-druha/>
// SPDX-FileCopyrightText: 2025 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.lang.lsp.ide.listeners

import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.plugin.powershell.lang.lsp.LanguageServer

class EditorLSPListener : EditorFactoryListener {

  override fun editorReleased(event: EditorFactoryEvent) {
    LanguageServer.editorClosed(event.editor)
  }

  override fun editorCreated(event: EditorFactoryEvent) {
    LanguageServer.editorOpened(event.editor)
  }
}
