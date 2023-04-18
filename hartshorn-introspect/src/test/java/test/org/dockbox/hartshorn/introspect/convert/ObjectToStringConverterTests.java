package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToStringConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ObjectToStringConverterTests {

    @Test
    void testNullCanBeConverted() {
        final Object element = null;
        final Converter<Object, String> converter = new ObjectToStringConverter();
        final Object converted = converter.convert(element);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof String);
        Assertions.assertEquals("null", converted);
    }

    @Test
    void testStringElementCanBeConvertedAndNotEscaped() {
        final String element = "test";
        final Converter<Object, String> converter = new ObjectToStringConverter();
        final Object converted = converter.convert(element);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof String);
        Assertions.assertEquals(element, converted);
    }

    @Test
    void testNonNullElementCanBeConverted() {
        final Object element = new Object();
        final Converter<Object, String> converter = new ObjectToStringConverter();
        final Object converted = converter.convert(element);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof String);
        Assertions.assertEquals(element.toString(), converted);
    }

    @Test
    void testToStringIsUsedForObjects() {
        final Object element = new TestClass("test");
        final Converter<Object, String> converter = new ObjectToStringConverter();
        final Object converted = converter.convert(element);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof String);
        Assertions.assertEquals("{toStringResult:test}", converted);
    }

    private record TestClass(String test) {
        @Override
            public String toString() {
                return "{toStringResult:" + this.test + "}";
            }
        }
}