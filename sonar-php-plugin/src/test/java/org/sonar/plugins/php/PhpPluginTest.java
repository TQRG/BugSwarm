/*
 * SonarQube PHP Plugin
 * Copyright (C) 2010-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/**
 *
 */
package org.sonar.plugins.php;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;
import org.sonar.api.Plugin;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.utils.Version;

import static org.assertj.core.api.Assertions.assertThat;

public class PhpPluginTest {

  @Test
  public void test() {
    SonarRuntime runtime = SonarRuntimeImpl.forSonarQube(Version.create(5, 6), SonarQubeSide.SCANNER);
    Plugin.Context context = new Plugin.Context(runtime);
    new PhpPlugin().define(context);

    assertThat(context.getExtensions()).hasSize(13);
  }

  @Test
  public void test_sonarlint() {
    SonarRuntime runtime = SonarRuntimeImpl.forSonarLint(Version.create(6, 0));
    Plugin.Context context = new Plugin.Context(runtime);
    new PhpPlugin().define(context);

    assertThat(context.getExtensions()).hasSize(9);
  }

  @Test
  public void should_contain_REPORT_PATHS_after_6_2() throws Exception {
    final Plugin.Context context6_2 = qubeContext(Version.create(6, 2));
    final PhpPlugin plugin = new PhpPlugin();
    plugin.define(context6_2);

    assertThat(extensionKeysOf(context6_2)).contains(PhpPlugin.PHPUNIT_COVERAGE_REPORT_PATHS_KEY);

    final Plugin.Context context6_1 = qubeContext(Version.create(6, 1));
    plugin.define(context6_1);
    assertThat(extensionKeysOf(context6_1)).doesNotContain(PhpPlugin.PHPUNIT_COVERAGE_REPORT_PATHS_KEY);
    assertThat(extensionKeysOf(context6_1)).contains(PhpPlugin.PHPUNIT_COVERAGE_REPORT_PATH_KEY);
  }

  private static Plugin.Context qubeContext(Version version) {
    final SonarRuntime runtime = SonarRuntimeImpl.forSonarQube(version, SonarQubeSide.SCANNER);
    return new Plugin.Context(runtime);
  }

  private static Set<String> extensionKeysOf(Plugin.Context context) {
    final List<Object> extensions = context.getExtensions();
    return extensions.stream().filter(obj -> obj instanceof PropertyDefinition).map(obj -> ((PropertyDefinition) obj).key()).collect(Collectors.toSet());
  }

}
