package org.stagemonitor.web.monitor.resteasy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.stagemonitor.core.metrics.metrics2.MetricName.name;
import static org.stagemonitor.requestmonitor.BusinessTransactionNamingStrategy.CLASS_NAME_DOT_METHOD_NAME;
import static org.stagemonitor.requestmonitor.BusinessTransactionNamingStrategy.CLASS_NAME_HASH_METHOD_NAME;
import static org.stagemonitor.requestmonitor.BusinessTransactionNamingStrategy.METHOD_NAME_SPLIT_CAMEL_CASE;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.Registry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.stagemonitor.core.CorePlugin;
import org.stagemonitor.core.configuration.Configuration;
import org.stagemonitor.core.metrics.metrics2.Metric2Registry;
import org.stagemonitor.requestmonitor.RequestMonitor;
import org.stagemonitor.requestmonitor.RequestMonitorPlugin;
import org.stagemonitor.web.WebPlugin;
import org.stagemonitor.web.monitor.HttpRequestTrace;
import org.stagemonitor.web.monitor.MonitoredHttpRequest;
import org.stagemonitor.web.monitor.filter.StatusExposingByteCountingServletResponse;

public class ResteasyRequestMonitorTest {
    private MockHttpServletRequest resteasyServletRequest = new MockHttpServletRequest("GET", "/test/requestName");
    private MockHttpServletRequest resteasyServletNotFoundRequest = new MockHttpServletRequest("GET", "/not-found");
    private MockHttpServletRequest nonResteasyServletRequest = new MockHttpServletRequest("GET", "/META-INF/resources/stagemonitor/static/jquery.js");
    private MockHttpRequest resteasyRequest;
    private MockHttpRequest notFoundRequest;
    private Configuration configuration = mock(Configuration.class);
    private RequestMonitorPlugin requestMonitorPlugin = mock(RequestMonitorPlugin.class);
    private WebPlugin webPlugin = mock(WebPlugin.class);
    private CorePlugin corePlugin = mock(CorePlugin.class);
    private RequestMonitor requestMonitor;
    private Metric2Registry registry = new Metric2Registry();
    private Registry getRequestNameRegistry;

    // the purpose of this class is to obtain a instance to a Method,
    // because Method objects can't be mocked as they are final
    private static class TestResource { public void testGetRequestName() {} }

    @Before
    public void before() throws Exception {
        resteasyRequest = MockHttpRequest.create(resteasyServletRequest.getMethod(), resteasyServletRequest.getRequestURI());
        notFoundRequest = MockHttpRequest.create(resteasyServletRequest.getMethod(), "not-found");
        getRequestNameRegistry = createRegistry(resteasyRequest, TestResource.class.getMethod("testGetRequestName"));
        resteasyServletRequest.getServletContext().setAttribute(Registry.class.getName(), getRequestNameRegistry);
        resteasyServletNotFoundRequest.getServletContext().setAttribute(Registry.class.getName(), getRequestNameRegistry);
        nonResteasyServletRequest.getServletContext().setAttribute(Registry.class.getName(), getRequestNameRegistry);
        registry.removeMatching(new MetricFilter() {
            @Override
            public boolean matches(String name, Metric metric) {
                return true;
            }
        });
        when(configuration.getConfig(RequestMonitorPlugin.class)).thenReturn(requestMonitorPlugin);
        when(configuration.getConfig(WebPlugin.class)).thenReturn(webPlugin);
        when(configuration.getConfig(CorePlugin.class)).thenReturn(corePlugin);
        when(corePlugin.isStagemonitorActive()).thenReturn(true);
        when(corePlugin.getThreadPoolQueueCapacityLimit()).thenReturn(1000);
        when(requestMonitorPlugin.isCollectRequestStats()).thenReturn(true);
        when(requestMonitorPlugin.getBusinessTransactionNamingStrategy()).thenReturn(METHOD_NAME_SPLIT_CAMEL_CASE);
        when(webPlugin.getGroupUrls()).thenReturn(Collections.singletonMap(Pattern.compile("(.*).js$"), "*.js"));
        requestMonitor = new RequestMonitor(configuration, registry);
        ResteasyRequestNameDeterminerInstrumenter.setRequestMonitorPlugin(requestMonitorPlugin);
    }

    private Registry createRegistry(final MockHttpRequest request, Method requestMappingMethod) {
        ResourceInvoker invoker = mock(ResourceMethodInvoker.class);
        when(invoker.getMethod()).thenReturn(requestMappingMethod);

        ArgumentMatcher<HttpRequest> httpRequestMatcher = new ArgumentMatcher<HttpRequest>() {
            @Override
            public boolean matches(Object argument) {
                if (argument == null) {
                    return false;
                }

                if (!HttpRequest.class.isAssignableFrom(argument.getClass())) {
                    return false;
                }

                HttpRequest other = (HttpRequest) argument;
                return request.getUri().getPath().equals(other.getUri().getPath())
                        && request.getHttpMethod().equals(other.getHttpMethod());
            }
        };

        Registry registry = mock(Registry.class);
        when(registry.getResourceInvoker(argThat(httpRequestMatcher))).thenReturn(invoker);
        return registry;
    }

    @Test
    public void testRequestMonitorResteasyRequest() throws Exception {
        when(webPlugin.isMonitorOnlyResteasyRequests()).thenReturn(false);

        MonitoredHttpRequest monitoredRequest = createMonitoredHttpRequest(resteasyServletRequest);
        registerAspect(monitoredRequest, getRequestNameRegistry.getResourceInvoker(resteasyRequest));
        final RequestMonitor.RequestInformation<HttpRequestTrace> requestInformation = requestMonitor.monitor(monitoredRequest);

        assertEquals(1, requestInformation.getRequestTimer().getCount());
        assertEquals("Test Get Request Name", requestInformation.getRequestName());
        assertEquals("Test Get Request Name", requestInformation.getRequestTrace().getName());
        assertEquals("/test/requestName", requestInformation.getRequestTrace().getUrl());
        assertEquals(Integer.valueOf(200), requestInformation.getRequestTrace().getStatusCode());
        assertEquals("GET", requestInformation.getRequestTrace().getMethod());
        Assert.assertNull(requestInformation.getExecutionResult());
        assertNotNull(registry.getTimers().get(name("response_time_server").tag("request_name", "Test Get Request Name").layer("All").build()));
        verify(monitoredRequest, times(1)).onPostExecute(anyRequestInformation());
    }

    @Test
    public void testRequestMonitorResteasyRequestWithClassHashMethodNaming() throws Exception {
        when(webPlugin.isMonitorOnlyResteasyRequests()).thenReturn(false);
        when(requestMonitorPlugin.getBusinessTransactionNamingStrategy()).thenReturn(CLASS_NAME_HASH_METHOD_NAME);

        MonitoredHttpRequest monitoredRequest = createMonitoredHttpRequest(resteasyServletRequest);
        registerAspect(monitoredRequest, getRequestNameRegistry.getResourceInvoker(resteasyRequest));
        final RequestMonitor.RequestInformation<HttpRequestTrace> requestInformation = requestMonitor.monitor(monitoredRequest);

        assertEquals(1, requestInformation.getRequestTimer().getCount());
        assertEquals("TestResource#testGetRequestName", requestInformation.getRequestName());
        assertEquals("TestResource#testGetRequestName", requestInformation.getRequestTrace().getName());
        assertEquals("/test/requestName", requestInformation.getRequestTrace().getUrl());
        assertEquals(Integer.valueOf(200), requestInformation.getRequestTrace().getStatusCode());
        assertEquals("GET", requestInformation.getRequestTrace().getMethod());
        Assert.assertNull(requestInformation.getExecutionResult());
        assertNotNull(registry.getTimers().get(name("response_time_server").tag("request_name", "TestResource#testGetRequestName").layer("All").build()));
        verify(monitoredRequest, times(1)).onPostExecute(anyRequestInformation());
    }


    @Test
    public void testRequestMonitorResteasyNotFoundException() throws Exception {
        when(webPlugin.isMonitorOnlyResteasyRequests()).thenReturn(false);
        when(requestMonitorPlugin.getBusinessTransactionNamingStrategy()).thenReturn(CLASS_NAME_HASH_METHOD_NAME);

        ArgumentMatcher<HttpRequest> notFoundRequestMatcher = new ArgumentMatcher<HttpRequest>() {
            @Override
            public boolean matches(Object argument) {
                if (argument == null) {
                    return false;
                }

                if (!HttpRequest.class.isAssignableFrom(argument.getClass())) {
                    return false;
                }

                HttpRequest other = (HttpRequest) argument;
                return notFoundRequest.getUri().getPath().equals(other.getUri().getPath())
                        && notFoundRequest.getHttpMethod().equals(other.getHttpMethod());
            }
        };

        when(getRequestNameRegistry.getResourceInvoker(argThat(notFoundRequestMatcher))).thenThrow(new NotFoundException());


        MonitoredHttpRequest monitoredRequest = createMonitoredHttpRequest(resteasyServletNotFoundRequest);
        final RequestMonitor.RequestInformation<HttpRequestTrace> requestInformation = requestMonitor.monitor(monitoredRequest);

        assertEquals(1, requestInformation.getRequestTimer().getCount());
        assertEquals("GET /not-found", requestInformation.getRequestName());
        assertEquals("GET /not-found", requestInformation.getRequestTrace().getName());
        assertEquals("/not-found", requestInformation.getRequestTrace().getUrl());
        assertEquals(Integer.valueOf(200), requestInformation.getRequestTrace().getStatusCode());
        assertEquals("GET", requestInformation.getRequestTrace().getMethod());
        Assert.assertNull(requestInformation.getExecutionResult());
        assertNotNull(registry.getTimers().get(name("response_time_server").tag("request_name", "GET /not-found").layer("All").build()));
        verify(monitoredRequest, times(1)).onPostExecute(anyRequestInformation());
        verify(monitoredRequest, times(1)).getRequestName();
    }

    @Test
    public void testRequestMonitorResteasyRequestWithClassDotMethodNaming() throws Exception {
        when(webPlugin.isMonitorOnlyResteasyRequests()).thenReturn(false);
        when(requestMonitorPlugin.getBusinessTransactionNamingStrategy()).thenReturn(CLASS_NAME_DOT_METHOD_NAME);

        MonitoredHttpRequest monitoredRequest = createMonitoredHttpRequest(resteasyServletRequest);
        registerAspect(monitoredRequest, getRequestNameRegistry.getResourceInvoker(resteasyRequest));
        final RequestMonitor.RequestInformation<HttpRequestTrace> requestInformation = requestMonitor.monitor(monitoredRequest);

        assertEquals(1, requestInformation.getRequestTimer().getCount());
        assertEquals("TestResource.testGetRequestName", requestInformation.getRequestName());
        assertEquals("TestResource.testGetRequestName", requestInformation.getRequestTrace().getName());
        assertEquals("/test/requestName", requestInformation.getRequestTrace().getUrl());
        assertEquals(Integer.valueOf(200), requestInformation.getRequestTrace().getStatusCode());
        assertEquals("GET", requestInformation.getRequestTrace().getMethod());
        Assert.assertNull(requestInformation.getExecutionResult());
        assertNotNull(registry.getTimers().get(name("response_time_server").tag("request_name", "TestResource.testGetRequestName").layer("All").build()));
        verify(monitoredRequest, times(1)).onPostExecute(anyRequestInformation());
    }

    @Test
    public void testRequestMonitorNonResteasyRequestDoMonitor() throws Exception {
        when(webPlugin.isMonitorOnlyResteasyRequests()).thenReturn(false);

        MonitoredHttpRequest monitoredRequest = createMonitoredHttpRequest(nonResteasyServletRequest);
        registerAspect(monitoredRequest, getRequestNameRegistry.getResourceInvoker(resteasyRequest));
        registerAspect(monitoredRequest, null);
        RequestMonitor.RequestInformation<HttpRequestTrace> requestInformation = requestMonitor.monitor(monitoredRequest);

        assertEquals(1, requestInformation.getRequestTimer().getCount());
        assertEquals("GET *.js", requestInformation.getRequestName());
        assertEquals("GET *.js", requestInformation.getRequestTrace().getName());
        assertNotNull(registry.getTimers().get(name("response_time_server").tag("request_name", "GET *.js").layer("All").build()));
        verify(monitoredRequest, times(1)).onPostExecute(anyRequestInformation());
        verify(monitoredRequest, times(1)).getRequestName();
    }

    @Test
    public void testRequestMonitorNonResteasyRequestDontMonitor() throws Exception {
        when(webPlugin.isMonitorOnlyResteasyRequests()).thenReturn(true);

        MonitoredHttpRequest monitoredRequest = createMonitoredHttpRequest(nonResteasyServletRequest);
        registerAspect(monitoredRequest, getRequestNameRegistry.getResourceInvoker(resteasyRequest));
        registerAspect(monitoredRequest, null);
        RequestMonitor.RequestInformation<HttpRequestTrace> requestInformation = requestMonitor.monitor(monitoredRequest);

        assertNull(requestInformation.getRequestTrace().getName());
        assertNull(registry.getTimers().get(name("response_time_server").tag("request_name", "GET *.js").layer("All").build()));
        verify(monitoredRequest, never()).onPostExecute(anyRequestInformation());
    }

    private void registerAspect(MonitoredHttpRequest monitoredRequest, final ResourceInvoker invoker) throws Exception {
		when(monitoredRequest.execute()).thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				ResteasyRequestNameDeterminerInstrumenter.setRequestNameByInvoker(invoker);
				return null;
			}
		});
    }

    private RequestMonitor.RequestInformation<HttpRequestTrace> anyRequestInformation() {
        return any();
    }

    private MonitoredHttpRequest createMonitoredHttpRequest(HttpServletRequest request) throws IOException {
        final StatusExposingByteCountingServletResponse response = new StatusExposingByteCountingServletResponse(new MockHttpServletResponse());
        return Mockito.spy(new MonitoredHttpRequest(request, response, new MockFilterChain(), configuration));
    }
}
