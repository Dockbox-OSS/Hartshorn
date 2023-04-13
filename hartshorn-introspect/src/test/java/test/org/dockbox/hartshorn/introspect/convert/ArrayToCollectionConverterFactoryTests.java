package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.ArrayToCollectionConverterFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({ "rawtypes" })
public class ArrayToCollectionConverterFactoryTests {

    @Test
    void testFactoryCreatesConverterForConcreteCollectionTarget() {
        final Introspector introspector = ConverterIntrospectionHelper.createIntrospectorForCollection(ArrayList.class, ArrayList::new);

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
        final Converter<Object[], Collection> converter = createConverter();
        Assertions.assertNotNull(converter);

        final Collection list = converter.convert(new Object[]{ "test" });
        Assertions.assertNotNull(list);
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("test", list.iterator().next());
    }

    @Test
    void testFactoryCreatesEmptyCollectionIfArrayEmpty() {
        final Converter<Object[], Collection> converter = createConverter();

        final Collection list = converter.convert(new Object[0]);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(0, list.size());
    }

    private static Converter<Object[], Collection> createConverter() {
        final Introspector introspector = ConverterIntrospectionHelper.createIntrospectorForCollection(Collection.class);
        final ConverterFactory<Object[], Collection<?>> factory = new ArrayToCollectionConverterFactory(introspector);

        final Converter<Object[], Collection> converter = factory.create(Collection.class);
        Assertions.assertNotNull(converter);

        return converter;
    }
}
