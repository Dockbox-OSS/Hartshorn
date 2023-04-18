package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.support.OptionalToOptionConverter;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class OptionalToOptionConverterTests {

    @Test
    void testPresentOptionalConvertsToPresentOption() {
        final Converter<Optional<?>, Option<?>> converter = new OptionalToOptionConverter();
        final Optional<String> option = Optional.of("test");
        final Option<?> converted = converter.convert(option);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted.present());
        Assertions.assertEquals("test", converted.get());
    }

    @Test
    void testEmptyOptionalConvertsToEmptyOption() {
        final Converter<Optional<?>, Option<?>> converter = new OptionalToOptionConverter();
        final Optional<String> option = Optional.empty();
        final Option<?> converted = converter.convert(option);
        Assertions.assertNotNull(converted);
        Assertions.assertFalse(converted.present());
    }
}