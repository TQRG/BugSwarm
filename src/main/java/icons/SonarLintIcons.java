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
package icons;

import com.intellij.openapi.util.IconLoader;
import javax.swing.Icon;

public class SonarLintIcons {
  public static final Icon ICON_SONARQUBE_16 = IconLoader.getIcon("/images/onde-sonar-16.png");
  public static final Icon ICON_SONARLINT_13 = IconLoader.getIcon("/images/ico-sonarlint-13.png");

  public static final Icon SONARLINT = IconLoader.getIcon("/images/sonarlint.png");

  public static final Icon PLAY = IconLoader.getIcon("/images/execute.png");
  public static final Icon CLEAN = IconLoader.getIcon("/images/clean.png");
  public static final Icon TOOLS = IconLoader.getIcon("/images/externalToolsSmall.png");
  public static final Icon SUSPEND = IconLoader.getIcon("/images/suspend.png");
  public static final Icon INFO = IconLoader.getIcon("/images/info.png");
  public static final Icon WARN = IconLoader.getIcon("/images/warn.png");

  private SonarLintIcons() {
    // only static
  }

  public static Icon severity(String severity) {
    return IconLoader.getIcon("/images/severity/" + severity.toLowerCase() + ".png");
  }
}
