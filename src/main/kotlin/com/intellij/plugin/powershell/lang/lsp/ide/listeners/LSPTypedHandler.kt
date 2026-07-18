// SPDX-FileCopyrightText: 2017 Guillaume Tâche
// SPDX-FileCopyrightText: 2017-2018 intellij-lsp contributors <https://github.com/gtache/intellij-lsp>
// SPDX-FileCopyrightText: 2018 Andrey Dernov <https://github.com/ant-druha/>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.lang.lsp.ide.listeners

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class LSPTypedHandler : TypedHandlerDelegate() {

  override fun charTyped(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
    return Result.CONTINUE
  }
}
