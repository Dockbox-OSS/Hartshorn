package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.convert.support.StringToUUIDConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class StringToUUIDConverterTests {

    @Test
    void testValidUUIDFormatCanConvert() {
        final StringToUUIDConverter converter = new StringToUUIDConverter();
        final UUID uuid = converter.convert("123e4567-e89b-12d3-a456-426655440000");
        Assertions.assertNotNull(uuid);
        Assertions.assertEquals("123e4567-e89b-12d3-a456-426655440000", uuid.toString());
    }

    @Test
    void testShortUUIDCanConvert() {
        final StringToUUIDConverter converter = new StringToUUIDConverter();
        final UUID uuid = converter.convert("1-2-3-4-5");
        Assertions.assertNotNull(uuid);
        Assertions.assertEquals("00000001-0002-0003-0004-000000000005", uuid.toString());
    }

    @Test
    void testInvalidUUIDFormatCannotConvert() {
        final StringToUUIDConverter converter = new StringToUUIDConverter();
        final UUID uuid = converter.convert("-123e4567-e89b-12d3-a456-426655440000");
        Assertions.assertNull(uuid);
    }

    @Test
    void testNullIsNotParsed() {
        final StringToUUIDConverter converter = new StringToUUIDConverter();
        final UUID uuid = converter.convert(null);
        Assertions.assertNull(uuid);
    }

}
