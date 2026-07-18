// SPDX-FileCopyrightText: 2017 Andrey Dernov <https://github.com/ant-druha/>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.ide.editor.formatting

import com.intellij.formatting.ASTBlock
import com.intellij.formatting.templateLanguages.BlockWithParent
import com.intellij.lang.ASTNode

interface PowerShellCodeBlock: ASTBlock, BlockWithParent {
  override fun getNode(): ASTNode?
}