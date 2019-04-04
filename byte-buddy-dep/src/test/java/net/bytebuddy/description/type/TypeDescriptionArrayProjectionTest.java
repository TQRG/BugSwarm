package net.bytebuddy.description.type;

import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.mockito.Mockito.mock;

public class TypeDescriptionArrayProjectionTest extends AbstractTypeDescriptionTest {

    @Override
    protected TypeDescription describe(Class<?> type) {
        return TypeDescription.ArrayProjection.of(new TypeDescription.ForLoadedType(type), 0);
    }

    @Override
    protected TypeDescription.Generic describeType(Field field) {
        return TypeDefinition.Sort.describe(field.getGenericType(), TypeDescription.Generic.AnnotationReader.DISPATCHER.resolve(field));
    }

    @Override
    protected TypeDescription.Generic describeReturnType(Method method) {
        return TypeDefinition.Sort.describe(method.getGenericReturnType(), TypeDescription.Generic.AnnotationReader.DISPATCHER.resolveReturnType(method));
    }

    @Override
    protected TypeDescription.Generic describeParameterType(Method method, int index) {
        return TypeDefinition.Sort.describe(method.getGenericParameterTypes()[index],
                TypeDescription.Generic.AnnotationReader.DISPATCHER.resolveParameterType(method, index));
    }

    @Override
    protected TypeDescription.Generic describeExceptionType(Method method, int index) {
        return TypeDefinition.Sort.describe(method.getGenericExceptionTypes()[index],
                TypeDescription.Generic.AnnotationReader.DISPATCHER.resolveExceptionType(method, index));
    }

    @Override
    protected TypeDescription.Generic describeSuperType(Class<?> type) {
        return TypeDefinition.Sort.describe(type.getGenericSuperclass(), TypeDescription.Generic.AnnotationReader.DISPATCHER.resolveSuperType(type));
    }

    @Override
    protected TypeDescription.Generic describeInterfaceType(Class<?> type, int index) {
        return TypeDefinition.Sort.describe(type.getGenericInterfaces()[index], TypeDescription.Generic.AnnotationReader.DISPATCHER.resolveInterface(type, index));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArity() throws Exception {
        TypeDescription.ArrayProjection.of(mock(TypeDescription.class), -1);
    }

    @Override
    @Test
    @Ignore("The Java reflection API suffers a bug that affects parsing of type variable bounds")
    public void testTypeVariableU() throws Exception {
        super.testTypeVariableU();
    }

    @Override
    @Test
    @Ignore("The Java reflection API suffers a bug that affects parsing of type variable bounds")
    public void testTypeVariableV() throws Exception {
        super.testTypeVariableV();
    }

    @Override
    @Test
    @Ignore("The Java reflection API suffers a bug that affects parsing of type variable bounds")
    public void testTypeVariableW() throws Exception {
        super.testTypeVariableW();
    }

    @Override
    @Test
    @Ignore("The Java reflection API suffers a bug that affects parsing of type variable bounds")
    public void testTypeVariableX() throws Exception {
        super.testTypeVariableX();
    }

    @Override
    @Test
    @Ignore("The Java reflection API does not currently support owner types")
    public void testTypeAnnotationOwnerType() throws Exception {
        super.testTypeAnnotationOwnerType();
    }
}
