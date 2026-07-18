// SPDX-FileCopyrightText: 2017 Guillaume Tâche
// SPDX-FileCopyrightText: 2017-2018 intellij-lsp contributors <https://github.com/gtache/intellij-lsp>
// SPDX-FileCopyrightText: 2018 Andrey Dernov <https://github.com/ant-druha/>
// SPDX-FileCopyrightText: 2023-2024 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.lang.lsp.ide.listeners

import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

class DocumentListenerImpl(private val coroutineScope: CoroutineScope) : LSPEditorListener(), DocumentListener {
  /**
   * Called before the text of the document is changed.
   *
   * @param event the event containing the information about the change.
   */
  override fun beforeDocumentChange(event: DocumentEvent) {}

  /**
   * Called after the text of the document has been changed.
   *
   * @param event the event containing the information about the change.
   */
  override fun documentChanged(event: DocumentEvent) {
    if (checkManager()) {
      coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) { editorManager?.documentChanged(event) }
    }
  }
}
