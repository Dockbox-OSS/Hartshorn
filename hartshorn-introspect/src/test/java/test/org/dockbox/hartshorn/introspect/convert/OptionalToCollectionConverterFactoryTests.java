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

package test.org.dockbox.hartshorn.introspect.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.dockbox.hartshorn.util.collections.CollectionUtilities;

import static org.assertj.core.api.Assertions.assertThat;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.OptionalToCollectionConverterFactory;
import org.junit.jupiter.api.Test;

class OptionalToCollectionConverterFactoryTests {

    @Test
    void emptyOptionalConvertsToEmptyCollection() {
        Converter<Optional<?>, ArrayList> converter = createConverter();
        Optional<String> option = Optional.empty();

        Collection<?> converted = converter.convert(option);
        assertThat(converted).isNotNull();
        assertThat(converted).isEmpty();
    }

    @Test
    void presentOptionalConvertsToCollectionWithElement() {
        Converter<Optional<?>, ArrayList> converter = createConverter();
        Optional<String> option = Optional.of("test");

        Collection<?> converted = converter.convert(option);
        assertThat(converted)
                .isNotEmpty()
                .hasSize(1);
        assertThat(CollectionUtilities.first(converted)).isEqualTo("test");
    }

    @SuppressWarnings("NonApiType")
    private static Converter<Optional<?>, ArrayList> createConverter() {
        Introspector introspector = ConverterIntrospectionHelper.createIntrospectorForCollection(ArrayList.class, ArrayList::new);
        ConverterFactory<Optional<?>, Collection<?>> factory = new OptionalToCollectionConverterFactory(introspector);
        return factory.create(ArrayList.class);
    }
}
