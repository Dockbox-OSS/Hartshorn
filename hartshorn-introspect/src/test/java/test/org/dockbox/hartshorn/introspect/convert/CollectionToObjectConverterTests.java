package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.convert.ConditionalConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.CollectionToObjectConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

public class CollectionToObjectConverterTests {

    @Test
    void testSingleElementCollectionOfTargetTypeCanConvert() {
        final Set<String> collection = Collections.singleton("test");
        final CollectionToObjectConverter converter = new CollectionToObjectConverter();
        Assertions.assertTrue(converter.canConvert(collection, String.class));

        final Object converted = converter.convert(collection, Set.class, String.class);
        Assertions.assertNotNull(converted);
        Assertions.assertEquals("test", converted);
    }

    @Test
    void testSingleElementCollectionOfDifferentTypeCannotConvert() {
        final Set<Integer> collection = Collections.singleton(1);
        final ConditionalConverter converter = new CollectionToObjectConverter();
        Assertions.assertFalse(converter.canConvert(collection, String.class));
    }

    @Test
    void testEmptyCollectionCannotConvert() {
        final Set<String> collection = Collections.emptySet();
        final ConditionalConverter converter = new CollectionToObjectConverter();
        Assertions.assertFalse(converter.canConvert(collection, String.class));
    }

    @Test
    void testMultiElementCollectionCannotConvert() {
        final Set<String> collection = Set.of("test", "test2");
        final ConditionalConverter converter = new CollectionToObjectConverter();
        Assertions.assertFalse(converter.canConvert(collection, String.class));
    }
}