// SPDX-FileCopyrightText: 2024-2026 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.lang

import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.plugin.powershell.testFramework.PowerShellCodeInsightTestBase
import com.intellij.testFramework.junit5.TestApplication
import org.junit.jupiter.api.Test

@TestApplication
class PowerShellCommenterTest : PowerShellCodeInsightTestBase() {

  @Test
  fun testLineCommentIndent() {
    codeInsightTestFixture.configureByText(
      "file.ps1", """
      {
          Write-Output before
          <caret>comment
          Write-Output after
      }
    """.trimIndent()
    )
    codeInsightTestFixture.performEditorAction(IdeActions.ACTION_COMMENT_LINE)
    codeInsightTestFixture.checkResult(
      """
      {
          Write-Output before
          #comment
          Write-Output after
      }
    """.trimIndent()
    )
  }

  @Test
  fun testCommentExtension() {
    codeInsightTestFixture.configureByText(
      "file.ps1", """
      <#<caret>
      #>
    """.trimIndent()
    )
    waitForEditorManagerCreated(codeInsightTestFixture.file.virtualFile.toNioPath())
    codeInsightTestFixture.performEditorAction(IdeActions.ACTION_EDITOR_ENTER)
    waitForEditorManagerCreated(codeInsightTestFixture.file.virtualFile.toNioPath())
    codeInsightTestFixture.checkResult(
      """
      <#
      <caret>
      #>
    """.trimIndent()
    )
    waitForEditorManagerCreated(codeInsightTestFixture.file.virtualFile.toNioPath())
  }
}
