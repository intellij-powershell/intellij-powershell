// SPDX-FileCopyrightText: 2021-2022 Andrey Dernov <https://github.com/ant-druha/>
// SPDX-FileCopyrightText: 2022-2026 intellij-powershell contributors <https://github.com/intellij-powershell/intellij-powershell>
//
// SPDX-License-Identifier: Apache-2.0

package com.intellij.plugin.powershell.lang.lsp.ide.settings;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.plugin.powershell.ide.MessagesBundle;
import com.intellij.plugin.powershell.lang.lsp.PowerShellSettings;
import com.intellij.plugin.powershell.lang.lsp.languagehost.PSLanguageHostUtils;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.HTMLEditorKitBuilder;
import com.intellij.util.ui.UIUtil;
import com.intellij.xml.util.XmlStringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class PowerShellExecutableChooserPanel extends JComponent {
  private final Logger LOG = Logger.getInstance(getClass());
  private JLabel psDetectedVersion;
  private TextFieldWithBrowseButton psExecutablePathTextFieldChooser;
  private JTextPane psPluginSettingsHintTextPane;
  @SuppressWarnings("unused")
  private JPanel myJpanel;

  public PowerShellExecutableChooserPanel(@Nullable String executablePath) {
    String globalSettingsPath = PowerShellSettings.getInstance().getState().getPowerShellExePath();
    updateExecutablePath(StringUtil.isEmpty(executablePath) ? globalSettingsPath : executablePath);
    configurePluginSettingsHint();
  }

  private void createUIComponents() {
    JBTextField textField = new JBTextField(0);
    textField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        String powerShellExePath = textField.getText();
        updatePowerShellVersionLabel(powerShellExePath);
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        String powerShellExePath = textField.getText();
        updatePowerShellVersionLabel(powerShellExePath);
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
      }
    });
    FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false);
    psExecutablePathTextFieldChooser = FormUIUtil.createTextFieldWithBrowseButton(textField, fileChooserDescriptor);
  }

  private void updatePowerShellVersionLabel(@NotNull String powerShellExePath) {
    var versionFuture = PSLanguageHostUtils.INSTANCE.getPowerShellVersion(powerShellExePath);
    versionFuture.exceptionally(throwable -> {
      LOG.debug("Exception when getting PowerShell version: ", throwable);
      if (getExecutablePath().equals(powerShellExePath)) {
        setPowerShellVersionLabelValue(null);
      }
      return null;
    }).thenAccept(version -> {
      if (version != null && getExecutablePath().equals(powerShellExePath)) {
        setPowerShellVersionLabelValue(version.toString());
      }
    });
  }

  public @NotNull String getExecutablePath() {
    return psExecutablePathTextFieldChooser.getText().trim();
  }

  public void updateExecutablePath(@Nullable String path) {
    if (StringUtil.isEmpty(path) || path.equals(getExecutablePath())) return;
    updatePowerShellVersionLabel(path);
    psExecutablePathTextFieldChooser.setText(path);
  }

  public @Nullable String getVersionValue() {
    String version = StringUtil.substringAfterLast(
      psDetectedVersion.getText(),
      MessagesBundle.message("ps.editor.services.detected.version.label"));
    return StringUtil.trim(version);
  }

  public void setPowerShellVersionLabelValue(@Nullable String version) {
    psDetectedVersion.setText(getLabeledText(version));
  }

  public @NotNull JPanel getRootPanel() {
    return myJpanel;
  }

  private void configurePluginSettingsHint() {
    String hintText = MessagesBundle.message("settings.powershell.plugin.settings.hint");
    psPluginSettingsHintTextPane.setEditorKit(HTMLEditorKitBuilder.simple());
    psPluginSettingsHintTextPane.setEditable(false);
    psPluginSettingsHintTextPane.setBackground(UIUtil.getWindowColor());
    Font defaultFont = psPluginSettingsHintTextPane.getFont();
    psPluginSettingsHintTextPane.setFont(defaultFont.deriveFont(Font.PLAIN, defaultFont.getSize() - 1f));
    psPluginSettingsHintTextPane.setForeground(UIUtil.getLabelFontColor(UIUtil.FontColor.BRIGHTER));
    psPluginSettingsHintTextPane.setText(XmlStringUtil.wrapInHtml(hintText));
  }

  @NotNull
  private String getLabeledText(@Nullable String version) {
    return MessagesBundle.message("ps.editor.services.detected.version.label") + " " + StringUtil.notNullize(version);
  }

}
