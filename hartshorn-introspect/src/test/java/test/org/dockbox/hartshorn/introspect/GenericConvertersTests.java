package test.org.dockbox.hartshorn.introspect;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.ConvertibleTypePair;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class GenericConvertersTests {

    @Test
    void testGenericConverterWithSingleTypePair() {
        final GenericConverter converter = new SimpleGenericConverter(Set.of(ConvertibleTypePair.of(Object.class, String.class)));
        final GenericConverters converters = new GenericConverters();
        converters.addConverter(converter);

        final GenericConverter locatedConverter = converters.getConverter(new Object(), String.class);
        Assertions.assertNotNull(locatedConverter);
        Assertions.assertSame(converter, locatedConverter);
    }

    @Test
    void testGenericConverterWithMultipleTypePairs() {
        final GenericConverter converter = new SimpleGenericConverter(Set.of(
                ConvertibleTypePair.of(Object.class, String.class),
                ConvertibleTypePair.of(Object.class, Integer.class)
        ));
        final GenericConverters converters = new GenericConverters();
        converters.addConverter(converter);

        final GenericConverter locatedStringConverter = converters.getConverter(new Object(), String.class);
        Assertions.assertNotNull(locatedStringConverter);
        Assertions.assertSame(converter, locatedStringConverter);

        final GenericConverter locatedIntegerConverter = converters.getConverter(new Object(), Integer.class);
        Assertions.assertNotNull(locatedIntegerConverter);
        Assertions.assertSame(converter, locatedIntegerConverter);
    }

    @Test
    void testGenericConverterWithMultipleTypePairsAndMultipleConverters() {
        final GenericConverter converter1 = new SimpleGenericConverter(Set.of(
                ConvertibleTypePair.of(Object.class, String.class),
                ConvertibleTypePair.of(Object.class, Integer.class)
        ));
        final GenericConverter converter2 = new SimpleGenericConverter(Set.of(
                ConvertibleTypePair.of(Object.class, String.class),
                ConvertibleTypePair.of(Object.class, Integer.class),
                // Additional type to ensure the converters aren't considered equal, as they are implemented
                // as the same record type.
                ConvertibleTypePair.of(Object.class, Long.class)
        ));
        final GenericConverters converters = new GenericConverters();
        converters.addConverter(converter1);
        converters.addConverter(converter2);

        Assertions.assertThrows(IllegalStateException.class, () -> converters.getConverter(new Object(), String.class));
        Assertions.assertThrows(IllegalStateException.class, () -> converters.getConverter(new Object(), Integer.class));
        Assertions.assertDoesNotThrow(() -> converters.getConverter(new Object(), Long.class));
    }

    private record SimpleGenericConverter(Set<ConvertibleTypePair> convertibleTypes) implements GenericConverter {
        @Override
        public @Nullable <I, O> Object convert(@Nullable final Object source, @NonNull final Class<I> sourceType, @NonNull final Class<O> targetType) {
            throw new UnsupportedOperationException("Not implemented");
        }
    }
}
