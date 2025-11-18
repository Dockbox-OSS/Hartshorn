/*
 * Copyright 2019-2025 the original author or authors.
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
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class GenericConvertersTests {

    @Test
    void genericConverterWithSingleTypePair() {
        GenericConverter converter = new SimpleGenericConverter(Set.of(ConvertibleTypePair.of(Object.class, String.class)));
        ConverterCache converters = new GenericConverters();
        converters.addConverter(converter);

        GenericConverter locatedConverter = converters.getConverter(new Object(), String.class);
        assertThat(locatedConverter)
                .isNotNull()
                .isSameAs(converter);
    }

    @Test
    void genericConverterWithMultipleTypePairs() {
        GenericConverter converter = new SimpleGenericConverter(Set.of(
                ConvertibleTypePair.of(Object.class, String.class),
                ConvertibleTypePair.of(Object.class, Integer.class)
        ));
        ConverterCache converters = new GenericConverters();
        converters.addConverter(converter);

        GenericConverter locatedStringConverter = converters.getConverter(new Object(), String.class);
        assertThat(locatedStringConverter)
                .isNotNull()
                .isSameAs(converter);

        GenericConverter locatedIntegerConverter = converters.getConverter(new Object(), Integer.class);
        assertThat(locatedIntegerConverter)
                .isNotNull()
                .isSameAs(converter);
    }

    @Test
    void genericConverterWithMultipleTypePairsAndMultipleConverters() {
        GenericConverter converter1 = new SimpleGenericConverter(Set.of(
                ConvertibleTypePair.of(Object.class, String.class),
                ConvertibleTypePair.of(Object.class, Integer.class)
        ));
        GenericConverter converter2 = new SimpleGenericConverter(Set.of(
                ConvertibleTypePair.of(Object.class, String.class),
                ConvertibleTypePair.of(Object.class, Integer.class),
                // Additional type to ensure the converters aren't considered equal, as they are implemented
                // as the same record type.
                ConvertibleTypePair.of(Object.class, Long.class)
        ));
        GenericConverters converters = new GenericConverters();
        converters.addConverter(converter1);
        converters.addConverter(converter2);

        assertThatExceptionOfType(AmbiguousConverterException.class).isThrownBy(() -> converters.getConverter(new Object(), String.class));
        assertThatExceptionOfType(AmbiguousConverterException.class).isThrownBy(() -> converters.getConverter(new Object(), Integer.class));
        assertThatCode(() -> converters.getConverter(new Object(), Long.class)).doesNotThrowAnyException();
    }

    private record SimpleGenericConverter(Set<ConvertibleTypePair> convertibleTypes) implements GenericConverter {
        @Override
        public @Nullable <I, O> Object convert(@Nullable Object source, @NonNull Class<I> sourceType, @NonNull Class<O> targetType) {
            throw new UnsupportedOperationException("Not implemented");
        }
    }
}
