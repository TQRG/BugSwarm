/*
 * Copyright (C) 2018 Square, Inc.
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

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import okhttp3.internal.RecordingHeadersListener;
import okhttp3.internal.http2.Header;
import okhttp3.mockwebserver.DuplexResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.tls.HandshakeCertificates;
import okio.BufferedSource;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;

import static junit.framework.TestCase.assertTrue;
import static okhttp3.TestUtil.defaultClient;
import static okhttp3.tls.internal.TlsUtil.localhost;
import static org.junit.Assert.assertEquals;

public final class DuplexTest {
  @Rule public final TestRule timeout = new Timeout(30_000, TimeUnit.MILLISECONDS);
  @Rule public final MockWebServer server = new MockWebServer();

  private HandshakeCertificates handshakeCertificates = localhost();
  private OkHttpClient client = defaultClient();

  @Test public void clientReadsHeadersDataHeadersData() throws IOException {
    server.enqueue(new MockResponse()
        .clearHeaders()
        .addHeader("h1", "v1")
        .addHeader("h2", "v2")
        .setBody(new DuplexResponseBody() {
          @Override public Header.Listener onRequest(RecordedRequest request,
              BufferedSource requestBodySource, HttpSink responseBodySink) throws IOException {
            responseBodySink.sink().writeUtf8("staten");
            responseBodySink.sink().flush();

            responseBodySink.headers(Headers.of("brooklyn", "zoo"));
            responseBodySink.sink().writeUtf8(" island");
            responseBodySink.sink().flush();

            responseBodySink.headers(Headers.of("toronto", "aquarium"));
            responseBodySink.sink().close();

            return null;
          }
        }));
    enableProtocol(Protocol.HTTP_2);

    Call call = client.newCall(new Request.Builder()
        .url(server.url("/"))
        .duplex("POST")
        .build());

    Response response = call.execute();

    RecordingHeadersListener headersListener = new RecordingHeadersListener();
    response.headersListener(headersListener);

    BufferedSource source = response.body().source();
    assertEquals("staten island", source.readUtf8());

    assertEquals(Headers.of("h1", "v1", "h2", "v2"), response.headers());

    assertEquals(Arrays.asList(Headers.of("brooklyn", "zoo"), Headers.of("toronto", "aquarium")),
        headersListener.takeAll());
    assertTrue(source.exhausted());
  }

  @Test public void serverReadsHeadersDataHeadersData() throws IOException {
    final RecordingHeadersListener serverHeadersListener = new RecordingHeadersListener();
    final AtomicReference<BufferedSource> requestBodySourceRef = new AtomicReference<>();

    server.enqueue(new MockResponse()
        .clearHeaders()
        .addHeader("h1", "v1")
        .addHeader("h2", "v2")
        .setBody(new DuplexResponseBody() {
          @Override public Header.Listener onRequest(RecordedRequest request,
              BufferedSource requestBodySource, HttpSink responseBodySink) throws IOException {
            responseBodySink.sink().close();

            requestBodySourceRef.set(requestBodySource);
            return serverHeadersListener;
          }
        }));
    enableProtocol(Protocol.HTTP_2);

    Call call = client.newCall(new Request.Builder()
        .url(server.url("/"))
        .duplex("POST")
        .build());

    Response response = call.execute();
    HttpSink httpSink = response.httpSink();
    httpSink.sink().writeUtf8("hey\n");
    httpSink.headers(Headers.of("a", "android"));
    httpSink.sink().writeUtf8("whats going on\n");
    httpSink.headers(Headers.of("b", "blackberry"));
    httpSink.sink().close();

    // check what the server received
    BufferedSource requestBodySource = requestBodySourceRef.get();
    assertEquals("hey", requestBodySource.readUtf8Line());
    assertEquals("whats going on", requestBodySource.readUtf8Line());
    assertTrue(requestBodySource.exhausted());
    assertEquals(Arrays.asList(Headers.of("a", "android"), Headers.of("b", "blackberry")),
        serverHeadersListener.takeAll());
  }

  // TODO(oldergod) write tests for headers discarded with 100 Continue

  /**
   * Tests that use this will fail unless boot classpath is set. Ex. {@code
   * -Xbootclasspath/p:/tmp/alpn-boot-8.0.0.v20140317}
   */
  private void enableProtocol(Protocol protocol) {
    enableTls();
    client = client.newBuilder()
        .protocols(Arrays.asList(protocol, Protocol.HTTP_1_1))
        .build();
    server.setProtocols(client.protocols());
  }

  private void enableTls() {
    client = client.newBuilder()
        .sslSocketFactory(
            handshakeCertificates.sslSocketFactory(), handshakeCertificates.trustManager())
        .hostnameVerifier(new RecordingHostnameVerifier())
        .build();
    server.useHttps(handshakeCertificates.sslSocketFactory(), false);
  }
}
