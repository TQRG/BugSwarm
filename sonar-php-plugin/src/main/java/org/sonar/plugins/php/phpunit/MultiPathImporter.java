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
package org.sonar.plugins.php.phpunit;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.sensor.SensorContext;

public class MultiPathImporter implements PhpUnitImporter {

  private static final Logger LOGGER = LoggerFactory.getLogger(MultiPathImporter.class);

  private final SingleFilePhpUnitImporter importer;
  private final String pathsKey;
  private final String msg;

  public MultiPathImporter(SingleFilePhpUnitImporter importer, String pathsKey, String msg) {
    this.importer = importer;
    this.pathsKey = pathsKey;
    this.msg = msg;
  }

  @Override
  public void parseReport(SensorContext context, Map<String, Integer> numberOfLinesOfCode) {
    final String[] paths = context.settings().getStringArray(pathsKey);
    if (paths == null) {
      LOGGER.info("No PHPUnit " + msg + " reports provided (see '" + pathsKey + "' property)");
      return;
    }
    for (String path : paths) {
      importer.importReport(path, context, numberOfLinesOfCode);
    }
  }
}
