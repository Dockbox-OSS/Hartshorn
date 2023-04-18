package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.TypeConstructorsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class ConverterIntrospectionHelper {

    public static <T extends Collection<?>> Introspector createIntrospectorForCollection(final Class<T> type, final Supplier<T> supplier) {
        final TypeConstructorsIntrospector<T> constructors = Mockito.mock(TypeConstructorsIntrospector.class);
        try {
            final Constructor<T> defaultConstructor = type.getConstructor();
            Assertions.assertNotNull(defaultConstructor);

            final ConstructorView<T> constructorView = Mockito.mock(ConstructorView.class);
            Mockito.when(constructorView.constructor()).thenReturn(defaultConstructor);
            Mockito.when(constructorView.create()).thenAnswer(invocation -> Attempt.of(supplier.get()));

            Mockito.when(constructors.defaultConstructor()).thenReturn(Option.of(constructorView));
        }
        catch (final NoSuchMethodException e) {
            Mockito.when(constructors.defaultConstructor()).thenReturn(Option.empty());
        }

        final TypeParametersIntrospector parametersIntrospector = Mockito.mock(TypeParametersIntrospector.class);
        Mockito.when(parametersIntrospector.from(Collection.class)).thenReturn(List.of());

        final TypeView<T> typeView = Mockito.mock(TypeView.class);
        Mockito.when(typeView.constructors()).thenReturn(constructors);
        Mockito.when(typeView.typeParameters()).thenReturn(parametersIntrospector);

        final Introspector introspector = Mockito.mock(Introspector.class);
        Mockito.when(introspector.introspect(type)).thenReturn(typeView);
        return introspector;
    }

    public static <T extends Collection<?>> Introspector createIntrospectorForCollection(final Class<T> type) {
        return createIntrospectorForCollection(type, () -> null);
    }
}
