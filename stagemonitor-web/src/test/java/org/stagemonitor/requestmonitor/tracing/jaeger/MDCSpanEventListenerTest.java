package org.stagemonitor.requestmonitor.tracing.jaeger;

import com.codahale.metrics.SharedMetricRegistries;
import com.uber.jaeger.Span;
import com.uber.jaeger.SpanContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;
import org.stagemonitor.core.CorePlugin;
import org.stagemonitor.core.MeasurementSession;
import org.stagemonitor.core.Stagemonitor;
import org.stagemonitor.requestmonitor.tracing.wrapper.SpanWrapper;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MDCSpanEventListenerTest {

	private MDCSpanEventListener mdcSpanInterceptor;
	private CorePlugin corePlugin;
	private SpanWrapper spanWrapper;

	@Before
	public void setUp() throws Exception {
		Stagemonitor.reset();
		SharedMetricRegistries.clear();
		this.corePlugin = mock(CorePlugin.class);
		when(corePlugin.isStagemonitorActive()).thenReturn(true);

		mdcSpanInterceptor = new MDCSpanEventListener(corePlugin);
		final Span span = mock(Span.class);
		final SpanContext spanContext = mock(SpanContext.class);
		when(spanContext.getTraceID()).thenReturn(1L);
		when(spanContext.getSpanID()).thenReturn(1L);
		when(spanContext.getParentID()).thenReturn(0L);
		when(span.context()).thenReturn(spanContext);
		spanWrapper = new SpanWrapper(span, null, 0, Collections.singletonList(mdcSpanInterceptor));
	}

	@After
	public void tearDown() throws Exception {
		Stagemonitor.reset();
		MDC.clear();
	}

	@Test
	public void testMdc() throws Exception {
		Stagemonitor.startMonitoring(new MeasurementSession("MDCSpanEventListenerTest", "testHost", "testInstance"));
		when(corePlugin.getMeasurementSession())
				.thenReturn(new MeasurementSession("MDCSpanEventListenerTest", "testHost", "testInstance"));
		mdcSpanInterceptor.onStart(spanWrapper);

		assertEquals("1", MDC.get("spanId"));
		assertEquals("1", MDC.get("traceId"));
		assertNull(MDC.get("parentId"));

		mdcSpanInterceptor.onFinish(spanWrapper, null, 0);
		assertEquals("testHost", MDC.get("host"));
		assertEquals("MDCSpanEventListenerTest", MDC.get("application"));
		assertEquals("testInstance", MDC.get("instance"));
		assertNull(MDC.get("spanId"));
		assertNull(MDC.get("traceId"));
		assertNull(MDC.get("parentId"));
	}

	@Test
	public void testMdcStagemonitorNotStarted() throws Exception {
		when(corePlugin.getMeasurementSession())
				.thenReturn(new MeasurementSession("MDCSpanEventListenerTest", "testHost", null));

		mdcSpanInterceptor.onStart(spanWrapper);
		assertEquals("testHost", MDC.get("host"));
		assertEquals("MDCSpanEventListenerTest", MDC.get("application"));
		assertNull(MDC.get("instance"));
		assertNull(MDC.get("spanId"));
		assertNull(MDC.get("traceId"));
		assertNull(MDC.get("parentId"));
		mdcSpanInterceptor.onFinish(spanWrapper, null, 0);
	}

	@Test
	public void testMDCStagemonitorDeactivated() throws Exception {
		when(corePlugin.isStagemonitorActive()).thenReturn(false);
		when(corePlugin.getMeasurementSession())
				.thenReturn(new MeasurementSession("MDCSpanEventListenerTest", "testHost", null));

		mdcSpanInterceptor.onStart(spanWrapper);

		assertNull(MDC.getCopyOfContextMap());
	}
}
