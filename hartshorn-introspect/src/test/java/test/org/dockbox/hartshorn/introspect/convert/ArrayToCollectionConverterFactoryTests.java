package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.TypeConstructorsIntrospector;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.ArrayToCollectionConverterFactory;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ArrayToCollectionConverterFactoryTests {

    @Test
    void testFactoryCreatesConverterForConcreteCollectionTarget() throws NoSuchMethodException {
        final Introspector introspector = createIntrospectorForCollection(ArrayList.class, ArrayList::new);

        final ConverterFactory<Object[], Collection<?>> factory = new ArrayToCollectionConverterFactory(introspector);
        final Converter<Object[], ArrayList> converter = factory.create(ArrayList.class);
        Assertions.assertNotNull(converter);

        final List<?> list = converter.convert(new Object[]{ "test" });
        Assertions.assertNotNull(list);
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("test", list.get(0));
    }

    @Test
    void testFactoryCreatesConverterForInterfaceCollectionTarget() {
        final ConverterFactory<Object[], Collection<?>> factory = new ArrayToCollectionConverterFactory(null);
        final Converter<Object[], Collection> converter = factory.create(Collection.class);
        Assertions.assertNotNull(converter);

        final Collection list = converter.convert(new Object[]{ "test" });
        Assertions.assertNotNull(list);
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("test", list.iterator().next());
    }

    @Test
    void testFactoryCreatesEmptyCollectionIfArrayEmpty() {
        final ConverterFactory<Object[], Collection<?>> factory = new ArrayToCollectionConverterFactory(null);
        final Converter<Object[], Collection> converter = factory.create(Collection.class);
        Assertions.assertNotNull(converter);

        final Collection list = converter.convert(new Object[0]);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(0, list.size());
    }

    private static <T extends Collection<?>> Introspector createIntrospectorForCollection(final Class<T> type, final Supplier<T> supplier) throws NoSuchMethodException {
        final Constructor<T> defaultConstructor = type.getConstructor();
        Assertions.assertNotNull(defaultConstructor);

        final ConstructorView<T> constructorView = Mockito.mock(ConstructorView.class);
        Mockito.when(constructorView.constructor()).thenReturn(defaultConstructor);
        Mockito.when(constructorView.create()).thenAnswer(invocation -> Attempt.of(supplier.get()));

        final TypeConstructorsIntrospector<T> constructors = Mockito.mock(TypeConstructorsIntrospector.class);
        Mockito.when(constructors.defaultConstructor()).thenReturn(Option.of(constructorView));

        final TypeView<T> typeView = Mockito.mock(TypeView.class);
        Mockito.when(typeView.constructors()).thenReturn(constructors);

        final Introspector introspector = Mockito.mock(Introspector.class);
        Mockito.when(introspector.introspect(type)).thenReturn(typeView);
        return introspector;
    }
}
