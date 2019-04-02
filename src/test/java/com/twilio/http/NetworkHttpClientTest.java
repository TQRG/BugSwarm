package com.twilio.http;

import com.twilio.exception.ApiConnectionException;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class NetworkHttpClientTest {

    @Tested
    private NetworkHttpClient client;

    private void setupGet(
        final int statusCode,
        String content,
        @Mocked final Request mockRequest,
        @Mocked final URL mockUrl,
        @Mocked final HttpClientBuilder mockBuilder,
        @Mocked final CloseableHttpClient mockClient,
        @Mocked final CloseableHttpResponse mockResponse,
        @Mocked final StatusLine mockStatusLine,
        @Mocked final HttpEntity mockEntity
    ) throws IOException {
        final InputStream stream = new ByteArrayInputStream(content.getBytes("UTF-8"));

        new Expectations() {{
            mockBuilder.setDefaultHeaders((Collection<Header>) any);
            result = mockBuilder;

            mockBuilder.build();
            result = mockClient;

            mockRequest.getMethod();
            result = HttpMethod.GET;

            mockRequest.constructURL();
            result = mockUrl;

            mockRequest.requiresAuthentication();
            result = false;

            mockClient.execute((HttpUriRequest) any);
            result = mockResponse;

            mockResponse.getEntity();
            result = mockEntity;

            mockEntity.isRepeatable();
            result = true;

            mockEntity.getContentLength();
            result = 1;

            mockEntity.getContent();
            result = stream;

            mockResponse.getStatusLine();
            result = mockStatusLine;

            mockStatusLine.getStatusCode();
            result = statusCode;

            mockResponse.getEntity();
            result = null;
        }};
    }

    private void setupPost(
            final int statusCode,
            String content,
            @Mocked final Request mockRequest,
            @Mocked final URL mockUrl,
            @Mocked final HttpURLConnection mockConn,
            @Mocked final HttpClientBuilder mockBuilder,
            @Mocked final CloseableHttpClient mockClient,
            @Mocked final CloseableHttpResponse mockResponse,
            @Mocked final StatusLine mockStatusLine,
            @Mocked final HttpEntity mockEntity,
            @Mocked final OutputStream mockOutputStream,
            @Mocked final OutputStreamWriter mockWriter
    ) throws IOException {
        final InputStream stream = new ByteArrayInputStream(content.getBytes("UTF-8"));

        new Expectations() {{
            mockBuilder.setDefaultHeaders((Collection<Header>) any);
            result = mockBuilder;

            mockBuilder.build();
            result = mockClient;

            mockRequest.getMethod();
            result = HttpMethod.POST;

            mockRequest.constructURL();
            result = mockUrl;

            mockRequest.requiresAuthentication();
            result = false;

            mockRequest.getPostParams();

            mockClient.execute((HttpUriRequest) any);
            result = mockResponse;

            mockResponse.getEntity();
            result = mockEntity;

            mockEntity.isRepeatable();
            result = true;

            mockEntity.getContentLength();
            result = 1;

            mockEntity.getContent();
            result = stream;

            mockResponse.getStatusLine();
            result = mockStatusLine;

            mockStatusLine.getStatusCode();
            result = statusCode;

            mockResponse.getEntity();
            result = null;

//            mockUrl.openConnection();
//            result = mockConn;
//
//            mockConn.setAllowUserInteraction(false);
//            mockConn.addRequestProperty("Accept", "application/json");
//            mockConn.addRequestProperty("Accept-Encoding", "utf-8");
//            mockConn.setInstanceFollowRedirects(true);
//            mockConn.setRequestMethod("POST");
//            mockConn.setDoOutput(true);
//            mockConn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            mockConn.connect();
//
//            mockRequest.encodeFormBody();
//            result = "foo=bar&baz=quux";
//
//            mockConn.getOutputStream();
//            result = mockOutputStream;
//
//            new OutputStreamWriter(mockOutputStream);
//            result = mockWriter;
//
//            mockWriter.write("foo=bar&baz=quux");
//            mockWriter.close();
//
//            mockConn.getResponseCode();
//            result = statusCode;
//
//            mockConn.getErrorStream();
//            result = null;
//
//            mockConn.getInputStream();
//            result = stream;
        }};
    }

    private void setupDelete(
            final int statusCode,
            String content,
            @Mocked final Request mockRequest,
            @Mocked final URL mockUrl,
            @Mocked final HttpURLConnection mockConn,
            @Mocked final HttpClientBuilder mockBuilder,
            @Mocked final CloseableHttpClient mockClient,
            @Mocked final CloseableHttpResponse mockResponse,
            @Mocked final StatusLine mockStatusLine,
            @Mocked final HttpEntity mockEntity,
            @Mocked final OutputStream mockOutputStream,
            @Mocked final OutputStreamWriter mockWriter
    ) throws IOException {
        final InputStream stream = new ByteArrayInputStream(content.getBytes("UTF-8"));

        new Expectations() {{
            mockBuilder.setDefaultHeaders((Collection<Header>) any);
            result = mockBuilder;

            mockBuilder.build();
            result = mockClient;

            mockRequest.getMethod();
            result = HttpMethod.DELETE;

            mockRequest.constructURL();
            result = mockUrl;

            mockRequest.requiresAuthentication();
            result = false;

            mockClient.execute((HttpUriRequest) any);
            result = mockResponse;

            mockResponse.getEntity();
            result = mockEntity;

            mockEntity.isRepeatable();
            result = true;

            mockEntity.getContentLength();
            result = 1;

            mockEntity.getContent();
            result = stream;

            mockResponse.getStatusLine();
            result = mockStatusLine;

            mockStatusLine.getStatusCode();
            result = statusCode;

            mockResponse.getEntity();
            result = null;

//            mockBuilder.setDefaultHeaders((Collection<Header>) any);
//            result = mockBuilder;
//
//            mockBuilder.build();
//            result = mockClient;
//
//            mockRequest.getMethod();
//            result = HttpMethod.DELETE;
//
//            mockRequest.constructURL();
//            result = mockUrl;
//
//            mockRequest.requiresAuthentication();
//            result = false;
//
//            mockUrl.openConnection();
//            result = mockConn;
//
//            mockConn.setAllowUserInteraction(false);
//            mockConn.addRequestProperty("Accept", "application/json");
//            mockConn.addRequestProperty("Accept-Encoding", "utf-8");
//            mockConn.setInstanceFollowRedirects(true);
//            mockConn.setRequestMethod("DELETE");
//
//            mockConn.connect();
//
//            mockConn.getResponseCode();
//            result = statusCode;
//
//            mockConn.getErrorStream();
//            result = null;
//
//            mockConn.getInputStream();
//            result = stream;
        }};
    }

    private void setupAuthedGet(
            final int statusCode,
            String content,
            @Mocked final Request mockRequest,
            @Mocked final URL mockUrl,
            @Mocked final HttpURLConnection mockConn,
            @Mocked final HttpClientBuilder mockBuilder,
            @Mocked final CloseableHttpClient mockClient,
            @Mocked final CloseableHttpResponse mockResponse,
            @Mocked final StatusLine mockStatusLine,
            @Mocked final HttpEntity mockEntity
    ) throws IOException {
        final InputStream stream = new ByteArrayInputStream(content.getBytes("UTF-8"));

        new Expectations() {{
            mockBuilder.setDefaultHeaders((Collection<Header>) any);
            result = mockBuilder;

            mockBuilder.build();
            result = mockClient;

            mockRequest.getMethod();
            result = HttpMethod.GET;

            mockRequest.constructURL();
            result = mockUrl;

            mockRequest.requiresAuthentication();
            result = true;

            mockRequest.getAuthString();
            result = "foo:bar";

            mockClient.execute((HttpUriRequest) any);
            result = mockResponse;

            mockResponse.getEntity();
            result = mockEntity;

            mockEntity.isRepeatable();
            result = true;

            mockEntity.getContentLength();
            result = 1;

            mockEntity.getContent();
            result = stream;

            mockResponse.getStatusLine();
            result = mockStatusLine;

            mockStatusLine.getStatusCode();
            result = statusCode;

            mockResponse.getEntity();
            result = null;
//
//            mockUrl.openConnection();
//            result = mockConn;
//
//            mockConn.setAllowUserInteraction(false);
//            mockConn.addRequestProperty("Accept", "application/json");
//            mockConn.addRequestProperty("Accept-Encoding", "utf-8");
//            mockConn.setInstanceFollowRedirects(true);
//            mockConn.setRequestMethod("GET");
//
//            mockConn.setRequestProperty("Authorization", "Basic Zm9vOmJhcg==");
//
//            mockConn.connect();
//
//            mockConn.getResponseCode();
//            result = statusCode;
//
//            mockConn.getErrorStream();
//            result = null;
//
//            mockConn.getInputStream();
//            result = stream;
        }};
    }

    private void setupErrorResponse(
            final int statusCode,
            String content,
            @Mocked final Request mockRequest,
            @Mocked final URL mockUrl,
            @Mocked final HttpURLConnection mockConn,
            @Mocked final HttpClientBuilder mockBuilder,
            @Mocked final CloseableHttpClient mockClient,
            @Mocked final CloseableHttpResponse mockResponse,
            @Mocked final StatusLine mockStatusLine,
            @Mocked final HttpEntity mockEntity
    ) throws IOException {
        final InputStream stream = new ByteArrayInputStream(content.getBytes("UTF-8"));

        new Expectations() {{
            mockBuilder.setDefaultHeaders((Collection<Header>) any);
            result = mockBuilder;

            mockBuilder.build();
            result = mockClient;

            mockRequest.getMethod();
            result = HttpMethod.GET;

            mockRequest.constructURL();
            result = mockUrl;

            mockRequest.requiresAuthentication();
            result = true;

            mockRequest.getAuthString();
            result = "foo:bar";

            mockClient.execute((HttpUriRequest) any);
            result = mockResponse;

            mockResponse.getEntity();
            result = mockEntity;

            mockEntity.isRepeatable();
            result = true;

            mockEntity.getContentLength();
            result = 1;

            mockEntity.getContent();
            result = stream;

            mockResponse.getStatusLine();
            result = mockStatusLine;

            mockStatusLine.getStatusCode();
            result = statusCode;

            mockResponse.getEntity();
            result = null;

//            mockConn.setAllowUserInteraction(false);
//            mockConn.addRequestProperty("Accept", "application/json");
//            mockConn.addRequestProperty("Accept-Encoding", "utf-8");
//            mockConn.setInstanceFollowRedirects(true);
//            mockConn.setRequestMethod("GET");
//
//            mockConn.setRequestProperty("Authorization", "Basic Zm9vOmJhcg==");
//            mockConn.connect();
//
//            mockConn.getResponseCode();
//            result = statusCode;
//
//            mockConn.getErrorStream();
//            result = stream;
        }};
    }

    @Test
    public void testGet(
        @Mocked final Request mockRequest,
        @Mocked final URL mockUrl,
        @Mocked final HttpClientBuilder mockBuilder,
        @Mocked final CloseableHttpClient mockClient,
        @Mocked final CloseableHttpResponse mockResponse,
        @Mocked final StatusLine mockStatusLine,
        @Mocked final HttpEntity mockEntity
    ) throws IOException {
        setupGet(
            200,
            "frobozz",
            mockRequest,
            mockUrl,
            mockBuilder,
            mockClient,
            mockResponse,
            mockStatusLine,
            mockEntity
        );

        client = new NetworkHttpClient(mockBuilder);
        Response resp = client.makeRequest(mockRequest);

        assertEquals(resp.getStatusCode(), 200);
        assertEquals(resp.getContent(), "frobozz");
    }

    @Test(expected = ApiConnectionException.class)
    public void testMakeRequestIOException(
        @Mocked final Request mockRequest,
        @Mocked final URL mockUrl,
        @Mocked final HttpClientBuilder mockBuilder,
        @Mocked final CloseableHttpClient mockClient,
        @Mocked final CloseableHttpResponse mockResponse
    ) throws IOException {
        new NonStrictExpectations() {{
            mockBuilder.setDefaultHeaders((Collection<Header>) any);
            result = mockBuilder;

            mockBuilder.build();
            result = mockClient;

            mockRequest.getMethod();
            result = HttpMethod.GET;

            mockRequest.constructURL();
            result = mockUrl;

            mockRequest.requiresAuthentication();
            result = true;

            mockRequest.getAuthString();
            result = "foo:bar";

            mockClient.execute((HttpUriRequest) any);
            result = new ApiConnectionException("foo");
        }};

        client = new NetworkHttpClient(mockBuilder);
        client.makeRequest(mockRequest);
        fail("ApiConnectionException was expected");
    }

    @Test
    public void testPost(
        @Mocked final Request mockRequest,
        @Mocked final URL mockUrl,
        @Mocked final HttpURLConnection mockConn,
        @Mocked final HttpClientBuilder mockBuilder,
        @Mocked final CloseableHttpClient mockClient,
        @Mocked final CloseableHttpResponse mockResponse,
        @Mocked final StatusLine mockStatusLine,
        @Mocked final HttpEntity mockEntity,
        @Mocked final OutputStream mockOutputStream,
        @Mocked final OutputStreamWriter mockWriter
    ) throws IOException {
        setupPost(
            201,
            "frobozz",
            mockRequest,
            mockUrl,
            mockConn,
            mockBuilder,
            mockClient,
            mockResponse,
            mockStatusLine,
            mockEntity,
            mockOutputStream,
            mockWriter
        );

        client = new NetworkHttpClient(mockBuilder);
        Response resp = client.makeRequest(mockRequest);

        assertEquals(resp.getStatusCode(), 201);
        assertEquals(resp.getContent(), "frobozz");
    }

    @Test
    public void testReliableRequest() {
        final HttpClient httpClient = new NetworkHttpClient();
        Request request = new Request(HttpMethod.GET, "/uri");

        new NonStrictExpectations(httpClient) {{
            httpClient.makeRequest((Request) any);
            result = new Response("", TwilioRestClient.HTTP_STATUS_CODE_NO_CONTENT);
        }};

        httpClient.reliableRequest(request);
    }

    @Test
    public void testReliableRequestWithRetries() {
        final HttpClient httpClient = new NetworkHttpClient();
        Request request = new Request(HttpMethod.GET, "/uri");

        new NonStrictExpectations(httpClient) {{
            httpClient.makeRequest((Request) any);
            result = null;
            times = 3;
        }};

        httpClient.reliableRequest(request);
    }

    @Test
    public void testReliableRequestWithRetries100() throws InterruptedException {
        final HttpClient httpClient = new NetworkHttpClient();
        Request request = new Request(HttpMethod.GET, "/uri");

        new NonStrictExpectations(httpClient) {{
            httpClient.makeRequest((Request) any);
            result = new Response("", 500);
        }};

        httpClient.reliableRequest(request);
    }

    @Test
    public void testDelete(
        @Mocked final Request mockRequest,
        @Mocked final URL mockUrl,
        @Mocked final HttpURLConnection mockConn,
        @Mocked final HttpClientBuilder mockBuilder,
        @Mocked final CloseableHttpClient mockClient,
        @Mocked final CloseableHttpResponse mockResponse,
        @Mocked final StatusLine mockStatusLine,
        @Mocked final HttpEntity mockEntity,
        @Mocked final OutputStream mockOutputStream,
        @Mocked final OutputStreamWriter mockWriter
    ) throws IOException {
        String content = "";
        final InputStream stream = new ByteArrayInputStream(content.getBytes("UTF-8"));

        setupDelete(
            204,
            "",
            mockRequest,
            mockUrl,
            mockConn,
            mockBuilder,
            mockClient,
            mockResponse,
            mockStatusLine,
            mockEntity,
            mockOutputStream,
            mockWriter
        );

        client = new NetworkHttpClient(mockBuilder);
        Response resp = client.makeRequest(mockRequest);

        assertEquals(resp.getStatusCode(), 204);
        assertEquals(resp.getContent(), "");
    }

    @Test
    public void testAuthedGet(
        @Mocked final Request mockRequest,
        @Mocked final URL mockUrl,
        @Mocked final HttpURLConnection mockConn,
        @Mocked final HttpClientBuilder mockBuilder,
        @Mocked final CloseableHttpClient mockClient,
        @Mocked final CloseableHttpResponse mockResponse,
        @Mocked final StatusLine mockStatusLine,
        @Mocked final HttpEntity mockEntity
    ) throws IOException {
        setupAuthedGet(
            200,
            "frobozz",
            mockRequest,
            mockUrl,
            mockConn,
            mockBuilder,
            mockClient,
            mockResponse,
            mockStatusLine,
            mockEntity
        );

        client = new NetworkHttpClient(mockBuilder);
        Response resp = client.makeRequest(mockRequest);

        assertEquals(resp.getStatusCode(), 200);
        assertEquals(resp.getContent(), "frobozz");
    }

    @Test
    public void testErrorResponse(
        @Mocked final Request mockRequest,
        @Mocked final URL mockUrl,
        @Mocked final HttpURLConnection mockConn,
        @Mocked final HttpClientBuilder mockBuilder,
        @Mocked final CloseableHttpClient mockClient,
        @Mocked final CloseableHttpResponse mockResponse,
        @Mocked final StatusLine mockStatusLine,
        @Mocked final HttpEntity mockEntity
    ) throws IOException {
        setupErrorResponse(
            404,
            "womp",
            mockRequest,
            mockUrl,
            mockConn,
            mockBuilder,
            mockClient,
            mockResponse,
            mockStatusLine,
            mockEntity
        );

        client = new NetworkHttpClient(mockBuilder);
        Response resp = client.makeRequest(mockRequest);

        assertEquals(resp.getStatusCode(), 404);
        assertEquals(resp.getContent(), "womp");
    }
}
