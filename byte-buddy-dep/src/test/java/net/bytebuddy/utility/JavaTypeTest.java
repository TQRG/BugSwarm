package net.bytebuddy.utility;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.generic.GenericTypeDescription;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.test.utility.ObjectPropertyAssertion;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.objectweb.asm.Opcodes;

import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Member;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class JavaTypeTest {

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    public void testMethodHandle() throws Exception {
        assertThat(JavaType.METHOD_HANDLE.getTypeStub().getName(), is("java.lang.invoke.MethodHandle"));
        assertThat(JavaType.METHOD_HANDLE.getTypeStub().getModifiers(), is(Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT));
        assertThat(JavaType.METHOD_HANDLE.getTypeStub().getSuperType(), is((GenericTypeDescription) TypeDescription.OBJECT));
        assertThat(JavaType.METHOD_HANDLE.getTypeStub().getInterfaces().size(), is(0));
    }

    @Test
    public void testMethodType() throws Exception {
        assertThat(JavaType.METHOD_TYPE.getTypeStub().getName(), is("java.lang.invoke.MethodType"));
        assertThat(JavaType.METHOD_TYPE.getTypeStub().getModifiers(), is(Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL));
        assertThat(JavaType.METHOD_TYPE.getTypeStub().getSuperType(), is((GenericTypeDescription) TypeDescription.OBJECT));
        assertThat(JavaType.METHOD_TYPE.getTypeStub().getInterfaces().size(), is(1));
        assertThat(JavaType.METHOD_TYPE.getTypeStub().getInterfaces().contains(new TypeDescription.ForLoadedType(Serializable.class)), is(true));
    }

    @Test
    public void testMethodTypesLookup() throws Exception {
        assertThat(JavaType.METHOD_HANDLES_LOOKUP.getTypeStub().getName(), is("java.lang.invoke.MethodHandles$Lookup"));
        assertThat(JavaType.METHOD_HANDLES_LOOKUP.getTypeStub().getModifiers(), is(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL));
        assertThat(JavaType.METHOD_HANDLES_LOOKUP.getTypeStub().getSuperType(), is((GenericTypeDescription) TypeDescription.OBJECT));
        assertThat(JavaType.METHOD_HANDLES_LOOKUP.getTypeStub().getInterfaces().size(), is(0));
    }

    @Test
    public void testCallSite() throws Exception {
        assertThat(JavaType.CALL_SITE.getTypeStub().getName(), is("java.lang.invoke.CallSite"));
        assertThat(JavaType.CALL_SITE.getTypeStub().getModifiers(), is(Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT));
        assertThat(JavaType.CALL_SITE.getTypeStub().getSuperType(), is((GenericTypeDescription) TypeDescription.OBJECT));
        assertThat(JavaType.CALL_SITE.getTypeStub().getInterfaces().size(), is(0));
    }

    @Test
    public void testExecutable() throws Exception {
        assertThat(JavaType.EXECUTABLE.getTypeStub().getName(), is("java.lang.reflect.Executable"));
        assertThat(JavaType.EXECUTABLE.getTypeStub().getModifiers(), is(Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT));
        assertThat(JavaType.EXECUTABLE.getTypeStub().getSuperType(), is((GenericTypeDescription) new TypeDescription.ForLoadedType(AccessibleObject.class)));
        assertThat(JavaType.EXECUTABLE.getTypeStub().getInterfaces().size(), is(2));
        assertThat(JavaType.EXECUTABLE.getTypeStub().getInterfaces().contains(new TypeDescription.ForLoadedType(Member.class)), is(true));
        assertThat(JavaType.EXECUTABLE.getTypeStub().getInterfaces().contains(new TypeDescription.ForLoadedType(GenericDeclaration.class)), is(true));
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testJava7Types() throws Exception {
        assertThat(JavaType.METHOD_HANDLE.load(), notNullValue(Class.class));
        assertThat(JavaType.METHOD_TYPE.load(), notNullValue(Class.class));
        assertThat(JavaType.METHOD_HANDLES_LOOKUP.load(), notNullValue(Class.class));
        assertThat(JavaType.CALL_SITE.load(), notNullValue(Class.class));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testJava8Types() throws Exception {
        assertThat(JavaType.EXECUTABLE.load(), notNullValue(Class.class));
    }

    @Test
    public void testObjectProperties() throws Exception {
        ObjectPropertyAssertion.of(JavaType.class).apply();
    }
}
