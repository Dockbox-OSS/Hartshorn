package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToEnumConverterFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringToEnumConverterFactoryTests {

    @Test
    void testMatchingNameCanConvert() {
        final Converter<String, Color> converter = createConverter();
        final Color red = converter.convert("RED");
        Assertions.assertNotNull(red);
        Assertions.assertSame(Color.RED, red);
    }

    @Test
    void testMatchingNameIgnoreCaseCanNotConvert() {
        final Converter<String, Color> converter = createConverter();
        final Color red = converter.convert("red");
        Assertions.assertNull(red);
    }

    @Test
    void testNonMatchingNameCanNotConvert() {
        final Converter<String, Color> converter = createConverter();
        final Color yellow = converter.convert("YELLOW");
        Assertions.assertNull(yellow);
    }

    private static Converter<String, Color> createConverter() {
        final ConverterFactory<String, Enum> factory = new StringToEnumConverterFactory();
        return factory.create(Color.class);
    }

    enum Color {
        RED,
        GREEN,
        BLUE,
    }
}