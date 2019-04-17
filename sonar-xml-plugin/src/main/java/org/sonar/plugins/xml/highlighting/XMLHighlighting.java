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

import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class XMLHighlighting {

  private XMLHighlighting() {
  }

  public static List<HighlightingData> getHighlightingData(File file, Charset charset) throws IOException, XMLStreamException {
    return getHighlightingData(new FileInputStream(file), new SourceFileOffsets(file, charset));
  }

  public static List<HighlightingData> getHighlightingData(String content) throws XMLStreamException {
    InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    return getHighlightingData(inputStream, new SourceFileOffsets(content));
  }

  public static List<HighlightingData> getHighlightingData(InputStream inputStream, SourceFileOffsets sourceFileOffsets) throws XMLStreamException {
    XMLStreamReader streamReader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);

    List<HighlightingData> highlighting = new ArrayList<>();

    if (sourceFileOffsets.content().startsWith("<?xml")) {
      highlighting.add(new HighlightingData(0, 5, "k"));
      int closingBracketStartOffset = getClosingBracketStartOffset(0, sourceFileOffsets.content());
      highlighting.add(new HighlightingData(closingBracketStartOffset - 1, closingBracketStartOffset + 1, "k"));

      highlightAttributes(highlighting, 5, closingBracketStartOffset, sourceFileOffsets.content());
    }

    while (streamReader.hasNext()) {
      Location prevLocation = streamReader.getLocation();
      streamReader.next();
      int startOffset = sourceFileOffsets.startOffset(streamReader.getLocation().getLineNumber(), streamReader.getLocation().getColumnNumber());

      switch (streamReader.getEventType()) {
        case XMLStreamConstants.START_ELEMENT:
          int closingBracketStartOffset = getClosingBracketStartOffset(startOffset, sourceFileOffsets.content());
          int endOffset = startOffset + getNameWithNamespaceLength(streamReader) + 1;
          highlightAttributes(highlighting, endOffset, closingBracketStartOffset, sourceFileOffsets.content());

          highlighting.add(new HighlightingData(startOffset, endOffset, "k"));
          highlighting.add(new HighlightingData(closingBracketStartOffset, closingBracketStartOffset + 1, "k"));
          break;

        case XMLStreamConstants.END_ELEMENT:
          closingBracketStartOffset = getClosingBracketStartOffset(startOffset, sourceFileOffsets.content());
          if (removePrevHighlightingIfEmptyElement(highlighting, streamReader, prevLocation)) {
            endOffset = startOffset + getNameWithNamespaceLength(streamReader) + 1;
            highlighting.add(new HighlightingData(startOffset, endOffset, "k"));
            highlighting.add(new HighlightingData(closingBracketStartOffset - 1, closingBracketStartOffset + 1, "k"));
          } else {
            highlighting.add(new HighlightingData(startOffset, closingBracketStartOffset + 1, "k"));
          }
          break;

        case XMLStreamConstants.CDATA:
          closingBracketStartOffset = getCDATAClosingBracketStartOffset(startOffset, sourceFileOffsets.content());
          highlighting.add(new HighlightingData(startOffset, startOffset + 9, "k"));
          highlighting.add(new HighlightingData(closingBracketStartOffset - 2, closingBracketStartOffset + 1, "k"));
          break;

        case XMLStreamConstants.DTD:
          closingBracketStartOffset = getClosingBracketStartOffset(startOffset, sourceFileOffsets.content());
          highlighting.add(new HighlightingData(startOffset, startOffset + 9, "j"));
          highlighting.add(new HighlightingData(closingBracketStartOffset, closingBracketStartOffset + 1, "j"));
          break;

        case XMLStreamConstants.COMMENT:
          highlighting.add(new HighlightingData(startOffset, startOffset + streamReader.getTextLength() + 7, "j"));
          break;

        default:
          break;
      }
    }

    return highlighting;
  }

  private static void highlightAttributes(List<HighlightingData> highlighting, int from, int to, String content) {
    int counter = from + 1;

    Integer startOffset = null;
    Character attributeValueQuote = null;

    while (counter < to) {
      char c = content.charAt(counter);

      if (startOffset == null && !Character.isWhitespace(c)) {
        startOffset = counter;
      }


      if (attributeValueQuote != null && attributeValueQuote == c) {
        highlighting.add(new HighlightingData(startOffset, counter + 1, "s"));
        counter++;
        startOffset = null;
        attributeValueQuote = null;
      }

      if (c == '=' && attributeValueQuote == null) {
        highlighting.add(new HighlightingData(startOffset, counter, "c"));

        do {
          counter++;
          c = content.charAt(counter);
        } while (c != '\'' && c != '"');

        startOffset = counter;
        attributeValueQuote = c;
      }


      counter++;
    }
  }

  private static boolean removePrevHighlightingIfEmptyElement(List<HighlightingData> highlighting, XMLStreamReader streamReader, Location prevLocation) {
    boolean isEmptyElement = prevLocation.getLineNumber() == streamReader.getLocation().getLineNumber()
      && prevLocation.getColumnNumber() == streamReader.getLocation().getColumnNumber();

    if (isEmptyElement) {
      highlighting.remove(highlighting.size() - 1);
      highlighting.remove(highlighting.size() - 1);
    }

    return isEmptyElement;
  }

  private static int getClosingBracketStartOffset(int startOffset, String content) {
    return getCommonClosingBracketStartOffset(startOffset, content, false);
  }

  private static int getCDATAClosingBracketStartOffset(int startOffset, String content) {
    return getCommonClosingBracketStartOffset(startOffset, content, true);
  }

  private static int getCommonClosingBracketStartOffset(int startOffset, String content, boolean isCDATA) {
    int counter = startOffset + 1;
    while (startOffset < content.length()) {
      if (content.charAt(counter) == '>' && (!isCDATA || content.charAt(counter - 1) == ']')) {
        return counter;
      }
      counter++;
    }

    throw new IllegalStateException("No \">\" found.");
  }

  private static int getNameWithNamespaceLength(XMLStreamReader streamReader) {
    int prefixLength = 0;
    if (!streamReader.getName().getPrefix().isEmpty()) {
      prefixLength = streamReader.getName().getPrefix().length() + 1;
    }

    return prefixLength + streamReader.getLocalName().length();
  }

}
