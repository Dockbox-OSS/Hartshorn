package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.convert.ConditionalConverter;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToVoidConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ObjectToVoidConverterTests {

    @Test
    void testCanConvertPrimitive() {
        final ConditionalConverter converter = new ObjectToVoidConverter();
        Assertions.assertTrue(converter.canConvert(null, void.class));
    }

    @Test
    void testCanConvertPrimitiveWrapper() {
        final ConditionalConverter converter = new ObjectToVoidConverter();
        Assertions.assertTrue(converter.canConvert(null, Void.class));
    }

    @Test
    void testCanConvertPrimitiveType() {
        final ConditionalConverter converter = new ObjectToVoidConverter();
        Assertions.assertTrue(converter.canConvert(null, Void.TYPE));
    }

    @Test
    void testVoidIsAlwaysNull() {
        final GenericConverter converter = new ObjectToVoidConverter();

        Assertions.assertNull(converter.convert(null, Object.class, void.class));
        Assertions.assertNull(converter.convert("test", Object.class, void.class));

        Assertions.assertNull(converter.convert(null, Object.class, Void.class));
        Assertions.assertNull(converter.convert("test", Object.class, Void.class));

        Assertions.assertNull(converter.convert(null, Object.class, Void.TYPE));
        Assertions.assertNull(converter.convert("test", Object.class, Void.TYPE));
    }
}