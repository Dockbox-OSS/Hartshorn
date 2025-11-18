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

import org.dockbox.hartshorn.util.introspect.convert.ConditionalConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.CollectionToObjectConverter;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CollectionToObjectConverterTests {

    @Test
    void singleElementCollectionOfTargetTypeCanConvert() {
        Set<String> collection = Collections.singleton("test");
        CollectionToObjectConverter converter = new CollectionToObjectConverter();
        assertThat(converter.canConvert(collection, String.class)).isTrue();

        Object converted = converter.convert(collection, Set.class, String.class);
        assertThat(converted)
                .isNotNull()
                .isEqualTo("test");
    }

    @Test
    void singleElementCollectionOfDifferentTypeCannotConvert() {
        Set<Integer> collection = Collections.singleton(1);
        ConditionalConverter converter = new CollectionToObjectConverter();
        assertThat(converter.canConvert(collection, String.class)).isFalse();
    }

    @Test
    void emptyCollectionCannotConvert() {
        Set<String> collection = Collections.emptySet();
        ConditionalConverter converter = new CollectionToObjectConverter();
        assertThat(converter.canConvert(collection, String.class)).isFalse();
    }

    @Test
    void multiElementCollectionCannotConvert() {
        Set<String> collection = Set.of("test", "test2");
        ConditionalConverter converter = new CollectionToObjectConverter();
        assertThat(converter.canConvert(collection, String.class)).isFalse();
    }
}