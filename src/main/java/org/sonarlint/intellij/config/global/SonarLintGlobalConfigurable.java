/*
 * SonarLint for IntelliJ IDEA
 * Copyright (C) 2015 SonarSource
 * sonarlint@sonarsource.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonarlint.intellij.config.global;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;

import java.awt.BorderLayout;
import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jetbrains.annotations.Nls;
import org.sonarlint.intellij.core.SonarLintEngineManager;
import org.sonarlint.intellij.messages.GlobalConfigurationListener;

public class SonarLintGlobalConfigurable implements Configurable, Configurable.NoScroll {
  private final SonarLintEngineManager serverManager;
  private JPanel rootPanel;
  private SonarQubeServerMgmtPanel serversPanel;
  private SonarLintGlobalSettingsPanel globalPanel;
  private Application app;
  private SonarLintGlobalSettings globalSettings;

  public SonarLintGlobalConfigurable() {
    this.app = ApplicationManager.getApplication();
    this.globalSettings = app.getComponent(SonarLintGlobalSettings.class);
    this.serverManager = app.getComponent(SonarLintEngineManager.class);
  }

  @Nls @Override public String getDisplayName() {
    return "SonarLint General Settings";
  }

  @Nullable @Override public String getHelpTopic() {
    return null;
  }

  @Nullable @Override public JComponent createComponent() {
    return getPanel();
  }

  @Override public boolean isModified() {
    return serversPanel.isModified(globalSettings) || globalPanel.isModified(globalSettings);
  }

  @Override public void apply() throws ConfigurationException {
    serversPanel.save(globalSettings);
    globalPanel.save(globalSettings);

    GlobalConfigurationListener globalConfigurationListener = app.getMessageBus().syncPublisher(GlobalConfigurationListener.TOPIC);
    globalConfigurationListener.applied(globalSettings.getSonarQubeServers(), globalSettings.isAutoTrigger());
    serverManager.reloadServers();
  }

  @CheckForNull
  public List<SonarQubeServer> getCurrentSettings() {
    if (serversPanel != null) {
      SonarLintGlobalSettings settings = new SonarLintGlobalSettings();
      serversPanel.save(settings);
      return settings.getSonarQubeServers();
    }

    return null;
  }

  @Override public void reset() {
    if (serversPanel != null) {
      serversPanel.load(globalSettings);
    }
    if (globalPanel != null) {
      globalPanel.load(globalSettings);
    }
  }

  @Override public void disposeUIResources() {
    if (rootPanel != null) {
      rootPanel.setVisible(false);
      rootPanel = null;
    }
    if (serversPanel != null) {
      serversPanel.dispose();
      serversPanel = null;
    }

    serverManager.reloadServers();
  }

  private JPanel getPanel() {
    if (rootPanel == null) {
      rootPanel = new JPanel(new BorderLayout());
      globalPanel = new SonarLintGlobalSettingsPanel(globalSettings);
      serversPanel = new SonarQubeServerMgmtPanel();
      rootPanel.add(globalPanel.getComponent(), BorderLayout.NORTH);
      rootPanel.add(serversPanel.getComponent(), BorderLayout.CENTER);
    }

    return rootPanel;
  }
}
