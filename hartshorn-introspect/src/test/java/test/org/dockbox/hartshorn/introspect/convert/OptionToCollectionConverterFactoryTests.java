package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.OptionToCollectionConverterFactory;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

public class OptionToCollectionConverterFactoryTests {

    @Test
    void testEmptyOptionalConvertsToEmptyCollection() {
        final Converter<Option<?>, ArrayList> converter = createConverter();
        final Option<String> option = Option.empty();

        final Collection<?> converted = converter.convert(option);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted.isEmpty());
    }

    @Test
    void testPresentOptionalConvertsToCollectionWithElement() {
        final Converter<Option<?>, ArrayList> converter = createConverter();
        final Option<String> option = Option.of("test");

        final Collection<?> converted = converter.convert(option);
        Assertions.assertNotNull(converted);
        Assertions.assertFalse(converted.isEmpty());
        Assertions.assertEquals(1, converted.size());
        Assertions.assertEquals("test", converted.iterator().next());
    }

    @Test
    void testSuccessSomeConvertsToCollectionWithElement() {
        final Converter<Option<?>, ArrayList> converter = createConverter();
        final Option<String> option = Attempt.of("test");

        final Collection<?> converted = converter.convert(option);
        Assertions.assertNotNull(converted);
        Assertions.assertFalse(converted.isEmpty());
        Assertions.assertEquals(1, converted.size());
        Assertions.assertEquals("test", converted.iterator().next());
    }

    @Test
    void testFailureSomeConvertsToCollectionWithElement() {
        final Converter<Option<?>, ArrayList> converter = createConverter();
        final Option<String> option = Attempt.of("test", new Exception());

        final Collection<?> converted = converter.convert(option);
        Assertions.assertNotNull(converted);
        Assertions.assertFalse(converted.isEmpty());
        Assertions.assertEquals(1, converted.size());
        Assertions.assertEquals("test", converted.iterator().next());
    }

    @Test
    void testFailureNoneConvertsToEmptyCollection() {
        final Converter<Option<?>, ArrayList> converter = createConverter();
        final Option<String> option = Attempt.of(new Exception());

        final Collection<?> converted = converter.convert(option);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted.isEmpty());
    }

    @Test
    void testSuccessNoneConvertsToEmptyCollection() {
        final Converter<Option<?>, ArrayList> converter = createConverter();
        final Option<String> option = Attempt.empty();

        final Collection<?> converted = converter.convert(option);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted.isEmpty());
    }

    private static Converter<Option<?>, ArrayList> createConverter() {
        final Introspector introspector = ConverterIntrospectionHelper.createIntrospectorForCollection(ArrayList.class, ArrayList::new);
        final ConverterFactory<Option<?>, Collection<?>> factory = new OptionToCollectionConverterFactory(introspector);
        return factory.create(ArrayList.class);
    }
}
