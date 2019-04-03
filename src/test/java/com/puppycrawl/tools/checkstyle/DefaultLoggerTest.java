////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2015 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;
import com.puppycrawl.tools.checkstyle.checks.blocks.RightCurlyCheck;

public class DefaultLoggerTest {

    @Test
    public void testCtor() {
        final OutputStream infoStream = new ByteArrayOutputStream();
        final OutputStream errorStream = new ByteArrayOutputStream();
        final DefaultLogger dl = new DefaultLogger(infoStream, true, errorStream, true);
        dl.addException(new AuditEvent(5000, "myfile"), new IllegalStateException("upsss"));
        dl.auditFinished(new AuditEvent(6000, "myfile"));
    }

    @Test
    public void testCtorWithTwoParameters() {
        final OutputStream infoStream = new ByteArrayOutputStream();
        final DefaultLogger dl = new DefaultLogger(infoStream, true);
        dl.addException(new AuditEvent(5000, "myfile"), new IllegalStateException("upsss"));
        dl.auditFinished(new AuditEvent(6000, "myfile"));
    }

    @Test
    public void testFormErrorMessagePrintSeveritySetToFalse() {
        final OutputStream infoStream = new ByteArrayOutputStream();
        final boolean printSeverity = false;
        final DefaultLogger dl =
            new DefaultLogger(infoStream, true, infoStream, false, printSeverity);
        final LocalizedMessage violationMessage = new LocalizedMessage(0, 0, "", "", null,
            SeverityLevel.WARNING, null, RightCurlyCheck.class, null);
        final AuditEvent event = new AuditEvent(RightCurlyCheck.class, "myfile", violationMessage);
        final String expected = "myfile:0:  [RightCurly]";
        final String actual = dl.formErrorMessage(event, event.getSeverityLevel());
        assertEquals(expected, actual);
    }

    @Test
    public void testFormErrorMessagePrintSeveritySetToTrue() {
        final OutputStream infoStream = new ByteArrayOutputStream();
        final boolean printSeverity = true;
        final DefaultLogger dl =
            new DefaultLogger(infoStream, true, infoStream, false, printSeverity);
        final LocalizedMessage violationMessage = new LocalizedMessage(0, 0, "", "", null,
            SeverityLevel.WARNING, null, RightCurlyCheck.class, null);
        final AuditEvent event = new AuditEvent(RightCurlyCheck.class, "myfile", violationMessage);
        final String expected = "[WARNING] myfile:0:  [RightCurly]";
        final String actual = dl.formErrorMessage(event, event.getSeverityLevel());
        assertEquals(expected, actual);
    }
}
