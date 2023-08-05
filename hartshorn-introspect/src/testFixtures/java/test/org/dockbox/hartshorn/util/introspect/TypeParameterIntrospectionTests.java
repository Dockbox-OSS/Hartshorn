package test.org.dockbox.hartshorn.util.introspect;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("rawtypes")
public abstract class TypeParameterIntrospectionTests {

    protected abstract Introspector introspector();

    private interface TypeWithoutTypeBounds<T> {}
    private interface TypeWithSingleTypeBound<T extends Number> {}
    private interface TypeWithMultipleTypeBounds<T extends Number & Function<?,?> & Predicate<?>> {}

    @Test
    public void testVariableGenericTypeWithSingleUpperbound() {
        final TypeView<TypeWithSingleTypeBound> typeView = this.introspector().introspect(TypeWithSingleTypeBound.class);
        Assertions.assertSame(TypeWithSingleTypeBound.class, typeView.type());

        this.testVariableWithExpectedBounds(typeView, Number.class);
    }

    @Test
    public void testVariableGenericTypeWithMultipleUpperbound() {
        final TypeView<TypeWithMultipleTypeBounds> typeView = this.introspector().introspect(TypeWithMultipleTypeBounds.class);
        Assertions.assertSame(TypeWithMultipleTypeBounds.class, typeView.type());

        this.testVariableWithExpectedBounds(typeView, Number.class, Function.class, Predicate.class);
    }

    @Test
    void testVariableGenericTypeWithoutExplicitUpperbounds() {
        final TypeView<TypeWithoutTypeBounds> typeView = this.introspector().introspect(TypeWithoutTypeBounds.class);
        Assertions.assertSame(TypeWithoutTypeBounds.class, typeView.type());

        this.testVariableWithExpectedBounds(typeView, Object.class);
    }

    private void testVariableWithExpectedBounds(final TypeView<?> type, final Class<?>... expectedBounds) {
        final List<TypeParameterView> inputTypes = type.typeParameters().allInput();
        Assertions.assertEquals(1, inputTypes.size());

        final TypeParameterView parameterView = inputTypes.get(0);
        Assertions.assertTrue(parameterView.isBounded());
        Assertions.assertTrue(parameterView.isVariable());

        final Set<TypeView<?>> upperBounds = parameterView.upperBounds();
        Assertions.assertEquals(expectedBounds.length, upperBounds.size());
        for (final Class<?> expectedBound : expectedBounds) {
            Assertions.assertTrue(upperBounds.stream().anyMatch(typeView -> typeView.type().equals(expectedBound)));
        }
    }
}
