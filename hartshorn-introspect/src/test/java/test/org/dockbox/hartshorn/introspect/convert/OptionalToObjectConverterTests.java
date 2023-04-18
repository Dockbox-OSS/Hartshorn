package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.support.OptionalToObjectConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class OptionalToObjectConverterTests {

    @Test
    void testPresentOptionalConvertsToObject() {
        final Converter<Optional<?>, Object> converter = new OptionalToObjectConverter();
        final Optional<String> option = Optional.of("test");
        final Object converted = converter.convert(option);
        Assertions.assertNotNull(converted);
        Assertions.assertEquals("test", converted);
    }

    @Test
    void testEmptyOptionalConvertsToNull() {
        final Converter<Optional<?>, Object> converter = new OptionalToObjectConverter();
        final Optional<String> option = Optional.empty();
        final Object converted = converter.convert(option);
        Assertions.assertNull(converted);
    }
}
