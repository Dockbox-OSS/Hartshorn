package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.convert.support.CollectionToArrayConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CollectionToArrayConverterTests {

    @Test
    void testConversionKeepsOrderAndElements() {
        final List<Object> list = List.of("test", 1, 2.0, true, new Object());

        final Object converted = new CollectionToArrayConverter().convert(list, List.class, Object[].class);
        Assertions.assertNotNull(converted);

        Assertions.assertTrue(converted instanceof Object[]);
        final Object[] array = (Object[]) converted;

        Assertions.assertEquals(list.size(), array.length);
        for (int i = 0; i < list.size(); i++) {
            Assertions.assertSame(list.get(i), array[i]);
        }
    }

    @Test
    void testComponentTypeIsRetained() {
        final List<String> list = List.of("test", "test2", "test3");

        final Object converted = new CollectionToArrayConverter().convert(list, List.class, String[].class);
        Assertions.assertNotNull(converted);

        Assertions.assertTrue(converted instanceof String[]);
        final String[] array = (String[]) converted;

        Assertions.assertEquals(list.size(), array.length);
        for (int i = 0; i < list.size(); i++) {
            Assertions.assertEquals(list.get(i), array[i]);
        }
    }
}
