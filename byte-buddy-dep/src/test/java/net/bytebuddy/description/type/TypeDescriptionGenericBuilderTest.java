package net.bytebuddy.description.type;

import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.test.utility.ObjectPropertyAssertion;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TypeDescriptionGenericBuilderTest extends AbstractTypeDescriptionGenericTest {

    @Override
    protected TypeDescription.Generic describeType(Field field) {
        return describe(field.getGenericType(), TypeDescription.Generic.AnnotationReader.DISPATCHER.resolve(field))
                .accept(TypeDescription.Generic.Visitor.Substitutor.ForAttachment.of(new FieldDescription.ForLoadedField(field)));
    }

    @Override
    protected TypeDescription.Generic describeReturnType(Method method) {
        return describe(method.getGenericReturnType(), TypeDescription.Generic.AnnotationReader.DISPATCHER.resolveReturnType(method))
                .accept(TypeDescription.Generic.Visitor.Substitutor.ForAttachment.of(new MethodDescription.ForLoadedMethod(method)));
    }

    @Override
    protected TypeDescription.Generic describeParameterType(Method method, int index) {
        return describe(method.getGenericParameterTypes()[index], TypeDescription.Generic.AnnotationReader.DISPATCHER.resolveParameterType(method, index))
                .accept(TypeDescription.Generic.Visitor.Substitutor.ForAttachment.of(new MethodDescription.ForLoadedMethod(method)));
    }

    @Override
    protected TypeDescription.Generic describeExceptionType(Method method, int index) {
        return describe(method.getGenericExceptionTypes()[index], TypeDescription.Generic.AnnotationReader.DISPATCHER.resolveExceptionType(method, index))
                .accept(TypeDescription.Generic.Visitor.Substitutor.ForAttachment.of(new MethodDescription.ForLoadedMethod(method)));
    }

    @Override
    protected TypeDescription.Generic describeSuperType(Class<?> type) {
        return describe(type.getGenericSuperclass(), TypeDescription.Generic.AnnotationReader.DISPATCHER.resolveSuperType(type))
                .accept(TypeDescription.Generic.Visitor.Substitutor.ForAttachment.of(new TypeDescription.ForLoadedType(type)));
    }

    @Override
    protected TypeDescription.Generic describeInterfaceType(Class<?> type, int index) {
        return describe(type.getGenericInterfaces()[index], TypeDescription.Generic.AnnotationReader.DISPATCHER.resolveInterface(type, index))
                .accept(TypeDescription.Generic.Visitor.Substitutor.ForAttachment.of(new TypeDescription.ForLoadedType(type)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoOwnerTypeWhenRequired() throws Exception {
        TypeDescription.Generic.Builder.parameterizedType(Foo.Bar.class, Object.class);
    }

    @Test
    public void testImplicitOwnerTypeWhenRequired() throws Exception {
        assertThat(TypeDescription.Generic.Builder.parameterizedType(Foo.class, Object.class).build().getOwnerType(),
                is(TypeDefinition.Sort.describe(getClass())));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOwnerTypeWhenNotRequired() throws Exception {
        TypeDescription.Generic.Builder.parameterizedType(Foo.class, Object.class, Collections.<Type>singletonList(Object.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalOwnerType() throws Exception {
        TypeDescription.Generic.Builder.parameterizedType(Foo.Bar.class, Object.class, Collections.<Type>singletonList(Foo.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonGenericOwnerType() throws Exception {
        TypeDescription.Generic.Builder.parameterizedType(Foo.Bar.class, Foo.class, Collections.<Type>singletonList(Foo.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenericOwnerType() throws Exception {
        TypeDescription.Generic.Builder.parameterizedType(new TypeDescription.ForLoadedType(Foo.Qux.class),
                TypeDescription.Generic.Builder.parameterizedType(Foo.class, Object.class).build(),
                Collections.<TypeDefinition>singletonList(TypeDescription.OBJECT));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncompatibleParameterTypeNumber() throws Exception {
        TypeDescription.Generic.Builder.parameterizedType(Foo.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForbiddenZeroArity() throws Exception {
        TypeDescription.Generic.Builder.rawType(Foo.class).asArray(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForbiddenNegativeType() throws Exception {
        TypeDescription.Generic.Builder.rawType(Foo.class).asArray(-1);
    }

    @Test
    public void testMultipleArityArray() throws Exception {
        assertThat(TypeDescription.Generic.Builder.rawType(Foo.class).asArray(2).build().getComponentType().getComponentType().represents(Foo.class), is(true));
    }

    @Test
    public void testObjectProperties() throws Exception {
        ObjectPropertyAssertion.of(TypeDescription.Generic.Builder.OfGenericArrayType.class).apply();
        ObjectPropertyAssertion.of(TypeDescription.Generic.Builder.OfNonGenericType.class).apply();
        ObjectPropertyAssertion.of(TypeDescription.Generic.Builder.OfParameterizedType.class).apply();
        ObjectPropertyAssertion.of(TypeDescription.Generic.Builder.OfTypeVariable.class).apply();
    }

    @Override
    @Test
    @Ignore("The Java reflection API does not currently support owner types")
    public void testTypeAnnotationOwnerType() throws Exception {
        super.testTypeAnnotationOwnerType();
    }

    private TypeDescription.Generic describe(Type type, TypeDescription.Generic.AnnotationReader annotationReader) {
        if (type instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) type;
            return wildcardType.getLowerBounds().length == 0
                    ? builder(wildcardType.getUpperBounds()[0], annotationReader.ofWildcardUpperBoundType(0)).asWildcardUpperBound(annotationReader.asList())
                    : builder(wildcardType.getLowerBounds()[0], annotationReader.ofWildcardLowerBoundType(0)).asWildcardLowerBound(annotationReader.asList());
        } else {
            return builder(type, annotationReader).build();
        }
    }

    private TypeDescription.Generic.Builder builder(Type type, TypeDescription.Generic.AnnotationReader annotationReader) {
        if (type instanceof TypeVariable) {
            return TypeDescription.Generic.Builder.typeVariable(((TypeVariable<?>) type).getName()).annotate(annotationReader.asList());
        } else if (type instanceof Class) {
            Class<?> rawType = (Class<?>) type;
            return (rawType.isArray()
                    ? builder(rawType.getComponentType(), annotationReader.ofComponentType()).asArray()
                    : TypeDescription.Generic.Builder.rawType((Class<?>) type)).annotate(annotationReader.asList());
        } else if (type instanceof GenericArrayType) {
            return builder(((GenericArrayType) type).getGenericComponentType(), annotationReader.ofComponentType()).asArray().annotate(annotationReader.asList());
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            List<TypeDescription.Generic> parameters = new ArrayList<TypeDescription.Generic>(parameterizedType.getActualTypeArguments().length);
            int index = 0;
            for (Type parameter : parameterizedType.getActualTypeArguments()) {
                parameters.add(describe(parameter, annotationReader.ofTypeArgument(index++)));
            }
            return TypeDescription.Generic.Builder.parameterizedType(new TypeDescription.ForLoadedType((Class<?>) parameterizedType.getRawType()),
                    parameterizedType.getOwnerType() == null
                            ? null
                            : describe(parameterizedType.getOwnerType(), annotationReader.ofOwnerType()),
                    parameters).annotate(annotationReader.asList());
        } else {
            throw new AssertionError("Unexpected type: " + type);
        }
    }

    @SuppressWarnings("unused")
    private static class Foo<T> {

        private class Bar<S> {
            /* empty */
        }

        private static class Qux<S> {
            /* empty */
        }
    }
}
