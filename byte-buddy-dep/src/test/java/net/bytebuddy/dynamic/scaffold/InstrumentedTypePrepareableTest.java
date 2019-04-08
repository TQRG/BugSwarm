package net.bytebuddy.dynamic.scaffold;

import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.test.utility.ObjectPropertyAssertion;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class InstrumentedTypePrepareableTest {

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private FieldDescription.Token fieldToken;

    @Mock
    private MethodDescription.Token methodToken;

    @Mock
    private InstrumentedType instrumentedType, transformed;

    @Test
    public void testNoOp() throws Exception {
        assertThat(InstrumentedType.Prepareable.NoOp.INSTANCE.prepare(instrumentedType), sameInstance(instrumentedType));
    }

    @Test
    public void testField() throws Exception {
        when(instrumentedType.withField(fieldToken)).thenReturn(transformed);
        assertThat(new InstrumentedType.Prepareable.FieldDefining(fieldToken).prepare(instrumentedType), sameInstance(transformed));
    }

    @Test
    public void testMethod() throws Exception {
        when(instrumentedType.withMethod(methodToken)).thenReturn(transformed);
        assertThat(new InstrumentedType.Prepareable.MethodDefining(methodToken).prepare(instrumentedType), sameInstance(transformed));
    }

    @Test
    public void testObjectProperties() throws Exception {
        ObjectPropertyAssertion.of(InstrumentedType.Prepareable.NoOp.class).apply();
        ObjectPropertyAssertion.of(InstrumentedType.Prepareable.FieldDefining.class).apply();
        ObjectPropertyAssertion.of(InstrumentedType.Prepareable.MethodDefining.class).apply();
    }
}
