package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToOptionConverter;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("rawtypes")
public class ObjectToOptionConverterTests {

    @Test
    void testNonNullElementConvertsToPresentOption() {
        final String element = "test";
        final Converter<Object, Option<?>> converter = new ObjectToOptionConverter();
        final Object converted = converter.convert(element);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Option);
        Assertions.assertTrue(((Option) converted).present());
        Assertions.assertEquals(element, ((Option) converted).get());
    }

    @Test
    void testNullElementConvertsToEmptyOption() {
        final Object element = null;
        final Converter<Object, Option<?>> converter = new ObjectToOptionConverter();
        final Object converted = converter.convert(element);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Option);
        Assertions.assertFalse(((Option) converted).present());
    }
}