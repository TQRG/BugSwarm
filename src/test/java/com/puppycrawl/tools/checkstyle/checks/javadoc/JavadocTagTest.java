////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2017 the original author or authors.
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

package com.puppycrawl.tools.checkstyle.checks.javadoc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.utils.JavadocUtils;

public class JavadocTagTest {

    /* Additional test for jacoco, since valueOf()
     * is generated by javac and jacoco reports that
     * valueOf() is uncovered.
     */
    @Test
    public void testJavadocTagTypeValueOf() {
        final JavadocUtils.JavadocTagType enumConst =
            JavadocUtils.JavadocTagType.valueOf("ALL");
        assertEquals(JavadocUtils.JavadocTagType.ALL, enumConst);
    }

    /* Additional test for jacoco, since values()
     * is generated by javac and jacoco reports that
     * values() is uncovered.
     */
    @Test
    public void testJavadocTagTypeValues() {
        final JavadocUtils.JavadocTagType[] enumConstants =
            JavadocUtils.JavadocTagType.values();
        final JavadocUtils.JavadocTagType[] expected = {
            JavadocUtils.JavadocTagType.BLOCK,
            JavadocUtils.JavadocTagType.INLINE,
            JavadocUtils.JavadocTagType.ALL,
        };
        assertArrayEquals(expected, enumConstants);
    }

    @Test
    public void testToString() {
        final JavadocTag javadocTag = new JavadocTag(0, 1, "author", "firstArg");

        final String result = javadocTag.toString();

        assertEquals("JavadocTag[tag='author' lineNo=0, columnNo=1, firstArg='firstArg']", result);
    }
}
