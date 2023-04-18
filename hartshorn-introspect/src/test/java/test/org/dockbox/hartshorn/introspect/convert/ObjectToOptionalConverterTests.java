package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToOptionalConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

@SuppressWarnings("rawtypes")
public class ObjectToOptionalConverterTests {

    @Test
    void testNonNullElementConvertsToPresentOptional() {
        final String element = "test";
        final Converter<Object, Optional<?>> converter = new ObjectToOptionalConverter();
        final Object converted = converter.convert(element);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Optional);
        Assertions.assertTrue(((Optional) converted).isPresent());
        Assertions.assertEquals(element, ((Optional) converted).get());
    }

    @Test
    void testNullElementConvertsToEmptyOptional() {
        final Object element = null;
        final Converter<Object, Optional<?>> converter = new ObjectToOptionalConverter();
        final Object converted = converter.convert(element);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Optional);
        Assertions.assertFalse(((Optional) converted).isPresent());
    }
}
