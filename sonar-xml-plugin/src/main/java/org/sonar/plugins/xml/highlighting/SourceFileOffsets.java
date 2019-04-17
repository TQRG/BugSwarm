/*
 * SonarQube XML Plugin
 * Copyright (C) 2010 SonarSource
 * sonarqube@googlegroups.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonar.plugins.xml.highlighting;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class SourceFileOffsets {
  private final int length;
  private final List<Integer> lineStartOffsets = Lists.newArrayList();
  private final String content;

  public SourceFileOffsets(String content) {
    this.content = content;
    this.length = content.length();
    initOffsets(content);
  }

  public SourceFileOffsets(File file, Charset charset) {
    this(fileContent(file, charset));
  }

  public String content() {
    return content;
  }

  private static String fileContent(File file, Charset charset) {
    String fileContent;
    try {
      fileContent = Files.toString(file, charset);
    } catch (IOException e) {
      throw new IllegalStateException("Could not read " + file, e);
    }
    return fileContent;
  }

  private void initOffsets(String toParse) {
    lineStartOffsets.add(0);
    int i = 0;
    while (i < length) {
      if (toParse.charAt(i) == '\n' || toParse.charAt(i) == '\r') {
        int nextLineStartOffset = i + 1;
        if (i < (length - 1) && toParse.charAt(i) == '\r' && toParse.charAt(i + 1) == '\n') {
          nextLineStartOffset = i + 2;
          i++;
        }
        lineStartOffsets.add(nextLineStartOffset);
      }
      i++;
    }
  }

  /**
   * @param line starts from 1
   * @param column starts from 1
   * @return startOffset (starting from 0)
   */
  public int startOffset(int line, int column) {
    int lineStartOffset = lineStartOffsets.get(line - 1);
    return lineStartOffset + (column - 1);
  }

}

