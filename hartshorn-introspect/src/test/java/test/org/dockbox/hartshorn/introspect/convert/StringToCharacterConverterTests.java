package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.convert.support.StringToCharacterConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringToCharacterConverterTests {

    @Test
    void testCanConvertLengthOne() {
        final StringToCharacterConverter converter = new StringToCharacterConverter();
        final Character character = converter.convert("a");
        Assertions.assertNotNull(character);
        Assertions.assertEquals('a', character);
    }

    @Test
    void testCanNotConvertLengthZero() {
        final StringToCharacterConverter converter = new StringToCharacterConverter();
        final Character character = converter.convert("");
        Assertions.assertNull(character);
    }

    @Test
    void testNullConvertsToNull() {
        final StringToCharacterConverter converter = new StringToCharacterConverter();
        final Character character = converter.convert(null);
        Assertions.assertNull(character);
    }
}
