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

import com.thoughtworks.xstream.XStreamException;
import java.io.File;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.sensor.SensorContext;

public abstract class SingleFilePhpUnitImporter implements PhpUnitImporter {

  private static final Logger LOGGER = LoggerFactory.getLogger(SingleFilePhpUnitImporter.class);

  private String reportPathKey;
  private String msg;

  protected SingleFilePhpUnitImporter(String reportPathKey, String msg) {
    this.reportPathKey = reportPathKey;
    this.msg = msg;
  }

  @Override
  public final void parseReport(SensorContext context, Map<String, Integer> numberOfLinesOfCode) {
    String reportPath = context.settings().getString(reportPathKey);
    if (reportPath != null) {
      importReport(reportPath, context, numberOfLinesOfCode);
    } else {
      LOGGER.info("No PHPUnit " + msg + " reports provided (see '" + reportPathKey + "' property)");
    }
  }

  final void importReport(String reportPath, SensorContext context, Map<String, Integer> numberOfLinesOfCode) {
    File xmlFile = getIOFile(reportPath, context);

    if (xmlFile.exists()) {
      LOGGER.info("Analyzing PHPUnit " + msg + " report: " + reportPath + " with " + toString());
      try {
        importReport(xmlFile, context, numberOfLinesOfCode);
      } catch (XStreamException e) {
        throw new IllegalStateException("Report file is invalid, plugin will stop.", e);
      }
    } else {
      LOGGER.info("PHPUnit xml " + msg + " report not found: " + reportPath);
    }
  }

  protected abstract void importReport(File coverageReportFile, SensorContext context, Map<String, Integer> numberOfLinesOfCode);

  /*
   * Returns a java.io.File for the given path.
   * If path is not absolute, returns a File with module base directory as parent path.
   */
  private static File getIOFile(String path, SensorContext context) {
    File file = new File(path);
    if (!file.isAbsolute()) {
      file = new File(context.fileSystem().baseDir(), path);
    }

    return file;
  }

}
