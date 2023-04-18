package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.convert.support.StringToBooleanConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringToBooleanConverterTests {
    // TODO: Implement tests for StringToBooleanConverter


    @Test
    void testTrueCanConvert() {
        final StringToBooleanConverter converter = new StringToBooleanConverter();
        final Boolean converted = converter.convert("true");
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted);
    }

    @Test
    void testFalseCanConvert() {
        final StringToBooleanConverter converter = new StringToBooleanConverter();
        final Boolean converted = converter.convert("false");
        Assertions.assertNotNull(converted);
        Assertions.assertFalse(converted);
    }

    @Test
    void testNonDefinedValuesConvertToFalse() {
        final StringToBooleanConverter converter = new StringToBooleanConverter();
        final Boolean converted = converter.convert("test");
        Assertions.assertNotNull(converted);
        Assertions.assertFalse(converted);
    }
}
