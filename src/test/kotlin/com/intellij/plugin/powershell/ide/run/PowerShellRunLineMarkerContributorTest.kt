// SPDX-FileCopyrightText: 2026 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.ide.run

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.plugin.powershell.testFramework.PowerShellCodeInsightTestBase
import com.intellij.plugin.powershell.testFramework.RunInEdt
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.junit5.TestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@TestApplication
@RunInEdt
class PowerShellRunLineMarkerContributorTest : PowerShellCodeInsightTestBase() {
  private val contributor = PowerShellRunLineMarkerContributor()

  @Test
  fun testScriptHasOneActionableMarker() {
    val markers = markersFor("script.ps1")

    assertEquals(1, markers.size)
    assertTrue(markers.single().actions.isNotEmpty())
  }

  @Test
  fun testScriptExtensionIsCaseInsensitive() {
    assertEquals(1, markersFor("script.PS1").size)
    assertEquals(1, markersFor("script.Ps1").size)
  }

  @Test
  fun testModuleAndDataFilesHaveNoMarkers() {
    assertEquals(emptyList<RunLineMarkerContributor.Info>(), markersFor("module.psm1"))
    assertEquals(emptyList<RunLineMarkerContributor.Info>(), markersFor("data.psd1"))
  }

  private fun markersFor(fileName: String): List<RunLineMarkerContributor.Info> {
    val file = codeInsightTestFixture.configureByText(fileName, "Write-Output 'hello'")
    return generateSequence(file.findElementAt(0)) { PsiTreeUtil.nextLeaf(it) }
      .mapNotNull(contributor::getInfo)
      .toList()
  }
}
