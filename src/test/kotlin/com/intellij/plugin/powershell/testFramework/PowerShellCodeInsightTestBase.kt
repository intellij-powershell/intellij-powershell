// SPDX-FileCopyrightText: 2025-2026 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.testFramework

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.plugin.powershell.ide.PluginProjectRoot
import com.intellij.plugin.powershell.lang.lsp.LanguageServer
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import com.intellij.testFramework.fixtures.impl.TempDirTestFixtureImpl
import com.intellij.testFramework.junit5.fixture.tempPathFixture
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.extension.ExtendWith
import java.nio.file.Path
import kotlin.time.Duration.Companion.seconds

@ExtendWith(EdtInterceptor::class)
open class PowerShellCodeInsightTestBase {
  val tempPathFixture = tempPathFixture()
  lateinit var tempPath: Path
  lateinit var codeInsightTestFixture: CodeInsightTestFixture
  val project: Project get() = codeInsightTestFixture.project

  @BeforeEach
  fun setupFixture(testInfo: TestInfo){
    tempPath = tempPathFixture.get()
    val factory = IdeaTestFixtureFactory.getFixtureFactory()
    val fixtureBuilder = factory.createFixtureBuilder(testInfo.displayName)
    val ideaProjectTestFixture = fixtureBuilder.getFixture()
    codeInsightTestFixture = factory.createCodeInsightFixture(
      ideaProjectTestFixture,
      TempDirTestFixtureImpl()
    )
    codeInsightTestFixture.testDataPath = getTestDataPath()
    codeInsightTestFixture.setUp()
  }

  @AfterEach
  fun tearDownEdt() {
    runInEdt {
      // codeInsightTestFixture.tearDown() will dispose of the project and then check for the leaked threads. To avoid
      // this, we need to wait for the plugin coroutine scope termination.
      terminatePluginCoroutineScope()

      codeInsightTestFixture.tearDown()
    }
  }

  open fun getTestDataPath(): String = tempPath.toString()

  fun waitForEditorConnects(path: Path) {
    runBlocking {
      withTimeout(20.seconds) {
        LanguageServer.getInstance(project).editorLanguageServer.value.apply {
          waitForInit()
          waitForEditorConnect(path)
        }
      }
    }
  }

  fun waitForEditorManagerCreated(path: Path) {
    runBlocking {
      withTimeout(20.seconds) {
        LanguageServer.getInstance(project).editorLanguageServer.value.apply {
          waitForEditorManagerCreated(path)
        }
      }
    }
  }

  private suspend fun terminatePluginCoroutineScope() {
    val logger = thisLogger()
    logger.info("Cancelling the plugin coroutine scope.")
    val scope = PluginProjectRoot.getInstance(project).coroutineScope
    scope.cancel()
    val job = scope.coroutineContext[Job]
    logger.info("Waiting for the plugin coroutine scope termination. Current status (isActive): ${job?.isActive}.")
    withTimeout(5.seconds) { job?.join() }
    logger.info("The plugin coroutine scope has been completed.")
  }
}
