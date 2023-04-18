package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.support.OptionToObjectConverterFactory;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OptionToObjectConverterFactoryTests {

    @Test
    void testPresentOptionConvertsToObject() {
        final OptionToObjectConverterFactory factory = new OptionToObjectConverterFactory();
        final Converter<Option<?>, String> converter = factory.create(String.class);
        final Option<String> option = Option.of("test");
        final String converted = converter.convert(option);
        Assertions.assertNotNull(converted);
        Assertions.assertEquals("test", converted);
    }

    @Test
    void testEmptyOptionConvertsToNull() {
        final OptionToObjectConverterFactory factory = new OptionToObjectConverterFactory();
        final Converter<Option<?>, String> converter = factory.create(String.class);
        final Option<String> option = Option.empty();
        final String converted = converter.convert(option);
        Assertions.assertNull(converted);
    }
}
