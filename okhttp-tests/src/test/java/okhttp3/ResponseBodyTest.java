/*
 * Copyright (C) 2016 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okhttp3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import okio.BufferedSource;
import okio.ByteString;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class ResponseBodyTest {
  @Test public void stringDefaultsToUtf8() throws IOException {
    ResponseBody body = body("68656c6c6f");
    assertEquals("hello", body.string());
  }

  @Test public void stringExplicitCharset() throws IOException {
    ResponseBody body = body("00000068000000650000006c0000006c0000006f", "utf-32be");
    assertEquals("hello", body.string());
  }

  @Test public void stringBomOverridesExplicitCharset() throws IOException {
    ResponseBody body = body("0000FFFF00000068000000650000006c0000006c0000006f", "utf-8");
    assertEquals("hello", body.string());
  }

  @Test public void stringBomUtf8() throws IOException {
    ResponseBody body = body("EFBBBF68656c6c6f");
    assertEquals("hello", body.string());
  }

  @Test public void stringBomUtf16Be() throws IOException {
    ResponseBody body = body("FEFF00680065006c006c006f");
    assertEquals("hello", body.string());
  }

  @Test public void stringBomUtf16Le() throws IOException {
    ResponseBody body = body("FFFE680065006c006c006f00");
    assertEquals("hello", body.string());
  }

  @Test public void stringBomUtf32Be() throws IOException {
    ResponseBody body = body("0000FFFF00000068000000650000006c0000006c0000006f");
    assertEquals("hello", body.string());
  }

  @Test public void stringBomUtf32Le() throws IOException {
    ResponseBody body = body("FFFF000068000000650000006c0000006c0000006f000000");
    assertEquals("hello", body.string());
  }

  @Test public void readerDefaultsToUtf8() throws IOException {
    ResponseBody body = body("68656c6c6f");
    assertEquals("hello", new BufferedReader(body.charStream()).readLine());
  }

  @Test public void readerExplicitCharset() throws IOException {
    ResponseBody body = body("00000068000000650000006c0000006c0000006f", "utf-32be");
    assertEquals("hello", new BufferedReader(body.charStream()).readLine());
  }

  @Test public void readerBomUtf8() throws IOException {
    ResponseBody body = body("EFBBBF68656c6c6f");
    assertEquals("hello", new BufferedReader(body.charStream()).readLine());
  }

  @Test public void readerBomUtf16Be() throws IOException {
    ResponseBody body = body("FEFF00680065006c006c006f");
    assertEquals("hello", new BufferedReader(body.charStream()).readLine());
  }

  @Test public void readerBomUtf16Le() throws IOException {
    ResponseBody body = body("FFFE680065006c006c006f00");
    assertEquals("hello", new BufferedReader(body.charStream()).readLine());
  }

  @Test public void readerBomUtf32Be() throws IOException {
    ResponseBody body = body("0000FFFF00000068000000650000006c0000006c0000006f");
    assertEquals("hello", new BufferedReader(body.charStream()).readLine());
  }

  @Test public void readerBomUtf32Le() throws IOException {
    ResponseBody body = body("FFFF000068000000650000006c0000006c0000006f000000");
    assertEquals("hello", new BufferedReader(body.charStream()).readLine());
  }

  @Test public void sourceSeesBom() throws IOException {
    ResponseBody body = body("EFBBFF68656C6C6F");
    BufferedSource source = body.source();
    assertEquals(0xEF, source.readByte() & 0xFF);
    assertEquals(0xBB, source.readByte() & 0xFF);
    assertEquals(0xFF, source.readByte() & 0xFF);
    assertEquals("hello", source.readUtf8());
  }

  @Test public void bytesSeesBom() throws IOException {
    ResponseBody body = body("EFBBFF68656C6C6F");
    byte[] bytes = body.bytes();
    assertEquals(0xEF, bytes[0] & 0xFF);
    assertEquals(0xBB, bytes[1] & 0xFF);
    assertEquals(0xFF, bytes[2] & 0xFF);
    assertEquals("hello", new String(bytes, 3, 5, "UTF-8"));
  }

  @Test public void byteStreamSeesBom() throws IOException {
    ResponseBody body = body("EFBBFF68656C6C6F");
    InputStream bytes = body.byteStream();
    assertEquals(0xEF, bytes.read());
    assertEquals(0xBB, bytes.read());
    assertEquals(0xFF, bytes.read());
    assertEquals("hello", new BufferedReader(new InputStreamReader(bytes)).readLine());
  }

  private static ResponseBody body(String hex) {
    return body(hex, null);
  }

  private static ResponseBody body(String hex, String charset) {
    MediaType mediaType = charset == null ? null : MediaType.parse("any/thing; charset=" + charset);
    return ResponseBody.create(mediaType, ByteString.decodeHex(hex).toByteArray());
  }
}
