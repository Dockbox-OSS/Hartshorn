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

import java.util.Collection;
import java.util.Set;

import org.dockbox.hartshorn.util.collections.CollectionUtilities;

import static org.assertj.core.api.Assertions.assertThat;

import org.dockbox.hartshorn.util.types.TypeUtils;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToCollectionConverterFactory;
import org.junit.jupiter.api.Test;

@SuppressWarnings("rawtypes")
class ObjectToCollectionConverterFactoryTests {

    @Test
    void nonNullElementCanBeConverted() {
        String element = "test";
        Converter<Object, Set<String>> converter = createConverter();
        Object converted = converter.convert(element);
        assertThat(converted).isNotNull();
        assertThat(converted).isInstanceOf(Set.class);
        assertThat(((Collection) converted)).hasSize(1);
        assertThat(CollectionUtilities.first((Iterable) converted)).isEqualTo(element);
    }

    @Test
    void primitiveCanBeConverted() {
        int element = 1;
        Converter<Object, Set<String>> converter = createConverter();
        Object converted = converter.convert(element);
        assertThat(converted).isNotNull();
        assertThat(converted).isInstanceOf(Set.class);
        assertThat(((Collection) converted)).hasSize(1);
        assertThat(CollectionUtilities.first((Iterable) converted)).isEqualTo(element);
    }

    private static Converter<Object, Set<String>> createConverter() {
        Introspector introspector = ConverterIntrospectionHelper.createIntrospectorForCollection(Set.class);
        ConverterFactory<Object, Collection<?>> factory = new ObjectToCollectionConverterFactory(introspector);
        return TypeUtils.unchecked(factory.create(Set.class), Converter.class);
    }
}
