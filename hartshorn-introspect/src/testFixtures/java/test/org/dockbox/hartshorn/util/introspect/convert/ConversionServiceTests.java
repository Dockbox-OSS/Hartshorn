package test.org.dockbox.hartshorn.util.introspect.convert;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.convert.StandardConversionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public abstract class ConversionServiceTests {

    protected abstract Introspector introspector();

    protected ConversionService conversionService() {
        return new StandardConversionService(this.introspector());
    }

    enum DemoABC {
        A, B, C,
    }

    @Test
    public void testConversionService() {
        final ConversionService conversionService = this.conversionService();
//        final Integer empty = conversionService.convert("", Integer.class);
//        Assertions.assertNotNull(empty);
//        Assertions.assertEquals(0, empty);
//
//        final Integer valid = conversionService.convert("12", Integer.class);
//        Assertions.assertNotNull(valid);
//        Assertions.assertEquals(12, valid);
//
//        final int primitive = conversionService.convert("12", int.class);
//        Assertions.assertEquals(12, primitive);
//
//        final DemoABC a = conversionService.convert("A", DemoABC.class);
//        Assertions.assertNotNull(a);
//        Assertions.assertSame(DemoABC.A, a);
//
//        final DemoABC d = conversionService.convert("D", DemoABC.class);
//        Assertions.assertNull(d);
//
//        final Option<?> option = conversionService.convert("test", Option.class);
//        Assertions.assertNotNull(option);
//        Assertions.assertTrue(option.present());
//        Assertions.assertEquals("test", option.get());
//
//        final String converted = conversionService.convert(option, String.class);
//        Assertions.assertNotNull(converted);
//        Assertions.assertEquals("test", converted);
//
//        final Option<?> optionFromNull = conversionService.convert(null, Option.class);
//        Assertions.assertNotNull(optionFromNull);
//        Assertions.assertFalse(optionFromNull.present());
//
//        final String stringFromNull = conversionService.convert(null, String.class);
//        Assertions.assertNotNull(stringFromNull);
//        Assertions.assertEquals("", stringFromNull);
//
//        final int intFromNull = conversionService.convert(null, int.class);
//        Assertions.assertEquals(0, intFromNull);
//
//        final Integer integerFromNull = conversionService.convert(null, Integer.class);
//        Assertions.assertNotNull(integerFromNull);
//        Assertions.assertEquals(0, integerFromNull);

        Set<?> set = conversionService.convert(null, HashSet.class);
        Assertions.assertNotNull(set);
        Assertions.assertTrue(set.isEmpty());
    }
}
