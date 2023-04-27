/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.org.dockbox.hartshorn.introspect;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.AmbiguousConverterException;
import org.dockbox.hartshorn.util.introspect.convert.ConverterCache;
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
        final ConverterCache converters = new GenericConverters();
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
        final ConverterCache converters = new GenericConverters();
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

        Assertions.assertThrows(AmbiguousConverterException.class, () -> converters.getConverter(new Object(), String.class));
        Assertions.assertThrows(AmbiguousConverterException.class, () -> converters.getConverter(new Object(), Integer.class));
        Assertions.assertDoesNotThrow(() -> converters.getConverter(new Object(), Long.class));
    }

    private record SimpleGenericConverter(Set<ConvertibleTypePair> convertibleTypes) implements GenericConverter {
        @Override
        public @Nullable <I, O> Object convert(@Nullable final Object source, @NonNull final Class<I> sourceType, @NonNull final Class<O> targetType) {
            throw new UnsupportedOperationException("Not implemented");
        }
    }
}
