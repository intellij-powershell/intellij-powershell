// SPDX-FileCopyrightText: 2017 Guillaume Tâche
// SPDX-FileCopyrightText: 2017-2018 intellij-lsp contributors <https://github.com/gtache/intellij-lsp>
// SPDX-FileCopyrightText: 2018 Andrey Dernov <https://github.com/ant-druha/>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.lang.lsp.languagehost

import org.eclipse.lsp4j.*

class ServerOptions(internal val syncKind: TextDocumentSyncKind?, internal val completionProvider: CompletionOptions?,
                    internal val signatureHelpProvider: SignatureHelpOptions, internal val codeLensProvider: CodeLensOptions?,
                    internal val documentOnTypeFormattingProvider: DocumentOnTypeFormattingOptions?,
                    internal val documentLinkProvider: DocumentLinkOptions?, internal val executeCommandProvider: ExecuteCommandOptions?)
