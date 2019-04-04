package net.bytebuddy.dynamic.scaffold.inline;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.generic.GenericTypeList;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.test.utility.ObjectPropertyAssertion;
import org.junit.Test;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.any;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RedefinitionDynamicTypeBuilderTest extends AbstractDynamicTypeBuilderForInliningTest {

    private static final String FOO = "foo", BAR = "bar";

    private static final String DEFAULT_METHOD_INTERFACE = "net.bytebuddy.test.precompiled.SingleDefaultMethodInterface";

    @Override
    protected DynamicType.Builder<?> create(Class<?> type) {
        return new ByteBuddy().redefine(type);
    }

    @Override
    protected DynamicType.Builder<?> createPlain() {
        return new ByteBuddy().redefine(Foo.class);
    }


    @Override
    protected DynamicType.Builder<?> create(TypeDescription typeDescription, ClassFileLocator classFileLocator) {
        return new ByteBuddy().redefine(typeDescription, classFileLocator);
    }


    @Test
    public void testConstructorRetentionNoAuxiliaryType() throws Exception {
        DynamicType.Unloaded<?> dynamicType = new ByteBuddy()
                .redefine(Bar.class)
                .make();
        assertThat(dynamicType.getRawAuxiliaryTypes().size(), is(0));
        Class<?> type = dynamicType.load(new URLClassLoader(new URL[0], null), ClassLoadingStrategy.Default.WRAPPER).getLoaded();
        assertThat(type.getDeclaredConstructors().length, is(1));
        assertThat(type.getDeclaredMethods().length, is(0));
        Field field = type.getDeclaredField(BAR);
        assertThat(field.get(type.getDeclaredConstructor(String.class).newInstance(FOO)), is((Object) FOO));
    }

    @Test
    public void testConstructorRebaseSingleAuxiliaryType() throws Exception {
        DynamicType.Unloaded<?> dynamicType = new ByteBuddy()
                .redefine(Bar.class)
                .constructor(any()).intercept(MethodCall.invoke(Object.class.getDeclaredConstructor()))
                .make();
        assertThat(dynamicType.getRawAuxiliaryTypes().size(), is(0));
        Class<?> type = dynamicType.load(new URLClassLoader(new URL[0], null), ClassLoadingStrategy.Default.WRAPPER).getLoaded();
        assertThat(type.getDeclaredConstructors().length, is(1));
        assertThat(type.getDeclaredMethods().length, is(0));
        Field field = type.getDeclaredField(BAR);
        assertThat(field.get(type.getDeclaredConstructor(String.class).newInstance(FOO)), nullValue(Object.class));
    }

    @Test
    public void testMethodRebase() throws Exception {
        DynamicType.Unloaded<?> dynamicType = new ByteBuddy()
                .redefine(Qux.class)
                .method(named(BAR)).intercept(StubMethod.INSTANCE)
                .make();
        assertThat(dynamicType.getRawAuxiliaryTypes().size(), is(0));
        Class<?> type = dynamicType.load(new URLClassLoader(new URL[0], null), ClassLoadingStrategy.Default.WRAPPER).getLoaded();
        assertThat(type.getDeclaredConstructors().length, is(1));
        assertThat(type.getDeclaredMethods().length, is(2));
        assertThat(type.getDeclaredMethod(FOO).invoke(null), nullValue(Object.class));
        assertThat(type.getDeclaredField(FOO).get(null), is((Object) FOO));
        assertThat(type.getDeclaredMethod(BAR).invoke(null), nullValue(Object.class));
        assertThat(type.getDeclaredField(FOO).get(null), is((Object) FOO));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testDefaultInterfaceSubInterface() throws Exception {
        Class<?> interfaceType = Class.forName(DEFAULT_METHOD_INTERFACE);
        Class<?> dynamicInterfaceType = new ByteBuddy()
                .redefine(interfaceType)
                .method(named(FOO)).intercept(new Implementation.Simple(new TextConstant(BAR), MethodReturn.REFERENCE))
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.CHILD_FIRST)
                .getLoaded();
        Class<?> dynamicClassType = new ByteBuddy()
                .subclass(dynamicInterfaceType)
                .make()
                .load(dynamicInterfaceType.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        assertThat(dynamicClassType.getMethod(FOO).invoke(dynamicClassType.newInstance()), is((Object) BAR));
        assertThat(dynamicInterfaceType.getDeclaredMethods().length, is(1));
        assertThat(dynamicClassType.getDeclaredMethods().length, is(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testObjectProperties() throws Exception {
        ObjectPropertyAssertion.of(RedefinitionDynamicTypeBuilder.class).create(new ObjectPropertyAssertion.Creator<List<?>>() {
            @Override
            public List<?> create() {
                TypeDescription typeDescription = mock(TypeDescription.class);
                when(typeDescription.asErasure()).thenReturn(typeDescription);
                return Collections.singletonList(typeDescription);
            }
        }).create(new ObjectPropertyAssertion.Creator<TypeDescription>() {
            @Override
            public TypeDescription create() {
                TypeDescription typeDescription = mock(TypeDescription.class);
                when(typeDescription.asErasure()).thenReturn(typeDescription);
                when(typeDescription.getInterfaces()).thenReturn(new GenericTypeList.Explicit(Collections.singletonList(typeDescription)));
                when(typeDescription.getDeclaredFields()).thenReturn(new FieldList.Empty());
                when(typeDescription.getDeclaredMethods()).thenReturn(new MethodList.Empty());
                return typeDescription;
            }
        }).apply();
    }

    public static class Bar {

        public final String bar;

        public Bar(String bar) {
            this.bar = bar;
        }
    }

    public static class Qux {

        public static String foo;

        public static String foo() {
            try {
                return foo;
            } finally {
                foo = FOO;
            }
        }

        public static String bar() {
            try {
                return foo;
            } finally {
                foo = FOO;
            }
        }
    }
}
