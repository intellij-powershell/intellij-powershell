// SPDX-FileCopyrightText: 2018-2021 Andrey Dernov <https://github.com/ant-druha/>
// SPDX-FileCopyrightText: 2023-2026 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.lang.lsp.ide.settings

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.plugin.powershell.ide.MessagesBundle
import com.intellij.plugin.powershell.ide.run.findPsExecutable
import com.intellij.plugin.powershell.lang.lsp.PowerShellSettings
import com.intellij.plugin.powershell.lang.lsp.languagehost.EditorServicesLanguageHostStarter
import com.intellij.plugin.powershell.lang.lsp.languagehost.PSLanguageHostUtils
import com.intellij.plugin.powershell.lang.lsp.languagehost.PowerShellNotInstalled
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.HTMLEditorKitBuilder
import com.intellij.util.ui.UIUtil
import com.intellij.xml.util.XmlStringUtil
import java.awt.Font
import javax.swing.JPanel
import javax.swing.JTextPane
import javax.swing.event.HyperlinkEvent

class PowerShellJPanelComponent {
  private val log = Logger.getInstance(javaClass)
  private val psEditorServicesDownloadLink = MessagesBundle.message("powershell.editor.services.download.link")

  private val isUseLanguageServerCheckBox = JBCheckBox(MessagesBundle.message("settings.powershell.lsp.is.enabled.box.text"))
  private val detectedESVersionLabel = JBLabel(MessagesBundle.message("ps.editor.services.detected.version.label"))
  private val pathToPSExtensionLabel = JBLabel(MessagesBundle.message("powershell.extension.path.form.label"))
  private val explanationTextPane = JTextPane()
  private val pathToPSExtDirTextField = JBTextField(0)
  private val psExtensionPathTextField: TextFieldWithBrowseButton = createExtensionPathField()
  private val psExecutableChooserPanel = PowerShellExecutableChooserPanel(null)

  private val panel = panel {
    row {
      cell(isUseLanguageServerCheckBox)
    }
    row {
      cell(psExecutableChooserPanel.rootPanel).resizableColumn().align(AlignX.FILL)
    }
    row {
      cell(pathToPSExtensionLabel)
      cell(psExtensionPathTextField).resizableColumn().align(AlignX.FILL)
    }
    row {
      cell(detectedESVersionLabel)
      cell(explanationTextPane).resizableColumn().align(AlignX.FILL)
    }
  }

  init {
    setVersionLabelVisible(false)
    configureExplanationText()
    isUseLanguageServerCheckBox.addChangeListener {
      psesConteolsSetEnabled(isUseLanguageServerCheckBox.isSelected)
    }
  }

  fun getMyPanel(): JPanel = panel

  fun setEditorServicesVersionLabelValue(version: String?) {
    detectedESVersionLabel.text = getLabeledText(version)
    val notEmpty = StringUtil.isNotEmpty(version)
    setVersionLabelVisible(notEmpty)
    detectedESVersionLabel.isEnabled = notEmpty && isUseLanguageServerCheckBox.isSelected &&
      !EditorServicesLanguageHostStarter.isUseBundledPowerShellExtension()
  }

  fun getPowerShellExtensionPath(): String = psExtensionPathTextField.text.trim()

  fun getPowerShellVersionValue(): String? = psExecutableChooserPanel.versionValue

  fun getPowerShellExePath(): String = psExecutableChooserPanel.executablePath

  fun getIsUseLanguageServer(): Boolean = isUseLanguageServerCheckBox.isSelected

  fun isUseLanguageServerSetSelected(value: Boolean) {
    isUseLanguageServerCheckBox.isSelected = value
    psesConteolsSetEnabled(isUseLanguageServerCheckBox.isSelected)
  }

  fun powerShellPathTextFieldSetEnabled(isEnabled: Boolean) {
    psExtensionPathTextField.isEnabled = isEnabled
  }

  fun fillPowerShellInfo(powerShellInfo: PowerShellSettings.PowerShellInfo) {
    setEditorServicesVersionLabelValue(powerShellInfo.editorServicesModuleVersion)
    setPowerShellExtensionPath(powerShellInfo.powerShellExtensionPath)
    setPowerShellExePath(powerShellInfo.powerShellExePath)
    psExecutableChooserPanel.setPowerShellVersionLabelValue(powerShellInfo.powerShellVersion)
    powerShellPathTextFieldSetEnabled(powerShellInfo.isUseLanguageServer)
    isUseLanguageServerSetSelected(powerShellInfo.isUseLanguageServer)
  }

  private fun createExtensionPathField(): TextFieldWithBrowseButton {
    val descriptor = object : FileChooserDescriptor(false, true, false, false, false, false) {
      override fun validateSelectedFiles(files: Array<out VirtualFile>) {
        if (files.isEmpty()) return
        val psEditorServicesPath = files[0].canonicalPath
        if (psEditorServicesPath == null) {
          setEditorServicesVersionLabelValue(null)
          return
        }
        if (getPowerShellExtensionPath() == psEditorServicesPath) return
        val psLanguageServerVersion = FormUIUtil.getEditorServicesVersion(psEditorServicesPath)
        setEditorServicesVersionLabelValue(psLanguageServerVersion)
      }
    }
    return FormUIUtil.createTextFieldWithBrowseButton(pathToPSExtDirTextField, descriptor)
  }

  private fun configureExplanationText() {
    val pathDescription = MessagesBundle.message("powershell.extension.path.form.description", psEditorServicesDownloadLink)
    applyHintStyle(explanationTextPane)
    explanationTextPane.text = XmlStringUtil.wrapInHtml(pathDescription)
    explanationTextPane.addHyperlinkListener { event: HyperlinkEvent ->
      if (event.eventType == HyperlinkEvent.EventType.ACTIVATED && psEditorServicesDownloadLink == event.description) {
        BrowserUtil.browse(event.description)
      }
    }
  }

  private fun applyHintStyle(textPane: JTextPane) {
    textPane.editorKit = HTMLEditorKitBuilder.simple()
    textPane.isEditable = false
    textPane.background = UIUtil.getWindowColor()
    val defaultFont = textPane.font
    textPane.font = defaultFont.deriveFont(Font.PLAIN, defaultFont.size - 1f)
    textPane.foreground = UIUtil.getLabelFontColor(UIUtil.FontColor.BRIGHTER)
  }

  private fun getLabeledText(version: String?): String {
    return MessagesBundle.message("ps.editor.services.detected.version.label") + " " + StringUtil.notNullize(version)
  }

  private fun setVersionLabelVisible(isVisible: Boolean) {
    detectedESVersionLabel.isVisible = isVisible
  }

  private fun psesConteolsSetEnabled(value: Boolean) {
    psExtensionPathTextField.isEnabled = value
    explanationTextPane.isEnabled = value
  }

  private fun setPowerShellExtensionPath(path: String?) {
    if (path.isNullOrEmpty()) {
      try {
        setBundledPowerShellExtensionPath()
      } catch (e: ConfigurationException) {
        log.warn("Can not detect bundled PowerShell Language Host: ", e)
      }
    } else {
      psExtensionPathTextField.text = path
      EditorServicesLanguageHostStarter.setUseBundledPowerShellExtension(false)
    }
  }

  private fun setPowerShellExePath(path: String?) {
    if (path == null) {
      try {
        psExecutableChooserPanel.updateExecutablePath(findPsExecutable())
      } catch (e: PowerShellNotInstalled) {
        log.warn("Can not find PowerShell executable in PATH: ", e)
      }
    } else {
      psExecutableChooserPanel.updateExecutablePath(path)
    }
  }

  @Throws(ConfigurationException::class)
  private fun setBundledPowerShellExtensionPath() {
    val version = FormUIUtil.getEditorServicesVersion(PSLanguageHostUtils.BUNDLED_PSES_PATH)
    pathToPSExtDirTextField.emptyText.text = "Bundled"
    setEditorServicesVersionLabelValue(version)
    detectedESVersionLabel.isEnabled = false
    EditorServicesLanguageHostStarter.setUseBundledPowerShellExtension(true)
  }
}
