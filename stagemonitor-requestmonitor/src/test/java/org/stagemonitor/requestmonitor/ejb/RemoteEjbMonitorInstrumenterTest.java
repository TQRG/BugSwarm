package org.stagemonitor.requestmonitor.ejb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.ejb.Remote;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.stagemonitor.core.Stagemonitor;
import org.stagemonitor.requestmonitor.RequestTrace;
import org.stagemonitor.requestmonitor.RequestTraceCapturingReporter;

public class RemoteEjbMonitorInstrumenterTest {

	private RemoteInterface remote = new RemoteInterfaceImpl();
	private RequestTraceCapturingReporter requestTraceCapturingReporter = new RequestTraceCapturingReporter();

	@BeforeClass
	@AfterClass
	public static void reset() {
		Stagemonitor.reset();
	}

	@Test
	public void testMonitorRemoteCalls() throws Exception {
		remote.foo();

		RequestTrace requestTrace = requestTraceCapturingReporter.get();
		assertNotNull(requestTrace);
		assertEquals("RemoteEjbMonitorInstrumenterTest$RemoteInterfaceImpl#foo", requestTrace.getName());
		assertEquals("void org.stagemonitor.requestmonitor.ejb.RemoteEjbMonitorInstrumenterTest$RemoteInterfaceImpl.foo()",
				requestTrace.getCallStack().getChildren().get(0).getSignature());
	}

	@Test
	public void testDontMonitorToString() throws Exception {
		remote.toString();

		RequestTrace requestTrace = requestTraceCapturingReporter.get();
		assertNull(requestTrace);
	}

	private interface RemoteInterface {
		void foo();
	}

	@Remote(RemoteInterface.class)
	public class RemoteInterfaceImpl implements RemoteInterface {

		@Override
		public void foo() {
		}

		@Override
		public String toString() {
			return super.toString();
		}
	}

}
