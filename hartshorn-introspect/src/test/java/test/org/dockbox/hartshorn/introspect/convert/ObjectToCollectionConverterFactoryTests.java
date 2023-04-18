package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToCollectionConverterFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Set;

@SuppressWarnings("rawtypes")
public class ObjectToCollectionConverterFactoryTests {

    @Test
    void testNonNullElementCanBeConverted() {
        final String element = "test";
        final Converter<Object, Set> converter = createConverter();
        final Object converted = converter.convert(element);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Set);
        Assertions.assertEquals(1, ((Set) converted).size());
        Assertions.assertEquals(element, ((Set) converted).iterator().next());
    }

    @Test
    void testPrimitiveCanBeConverted() {
        final int element = 1;
        final Converter<Object, Set> converter = createConverter();
        final Object converted = converter.convert(element);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Set);
        Assertions.assertEquals(1, ((Set) converted).size());
        Assertions.assertEquals(element, ((Set) converted).iterator().next());
    }

    private static Converter<Object, Set> createConverter() {
        final Introspector introspector = ConverterIntrospectionHelper.createIntrospectorForCollection(Set.class);
        final ConverterFactory<Object, Collection<?>> factory = new ObjectToCollectionConverterFactory(introspector);
        return factory.create(Set.class);
    }
}
