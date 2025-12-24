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
import java.util.List;

import org.dockbox.hartshorn.util.collections.CollectionUtilities;

import static org.assertj.core.api.Assertions.assertThat;
import org.dockbox.hartshorn.util.types.TypeUtils;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.ArrayToCollectionConverterFactory;
import org.junit.jupiter.api.Test;

@SuppressWarnings("rawtypes")
class ArrayToCollectionConverterFactoryTests {

    @Test
    void factoryCreatesConverterForConcreteCollectionTarget() {
        Introspector introspector = ConverterIntrospectionHelper.createIntrospectorForCollection(
            ArrayList.class,
            ArrayList::new);

        ConverterFactory<Object[], Collection<?>> factory =
            new ArrayToCollectionConverterFactory(introspector);
        Converter<Object[], ArrayList> converter = factory.create(ArrayList.class);
        assertThat(converter).isNotNull();

        List<?> list = converter.convert(new Object[] {"test"});
        assertThat(list)
                .hasSize(1);
        assertThat(list.get(0)).isEqualTo("test");
    }

    @Test
    void factoryCreatesConverterForInterfaceCollectionTarget() {
        Converter<Object[], Collection<String>> converter = createConverter();
        assertThat(converter).isNotNull();

        Collection<String> list = converter.convert(new Object[] {"test"});
        assertThat(list)
                .hasSize(1);
        assertThat(CollectionUtilities.first(list)).isEqualTo("test");
    }

    @Test
    void factoryCreatesEmptyCollectionIfArrayEmpty() {
        Converter<Object[], Collection<String>> converter = createConverter();

        Collection list = converter.convert(new Object[0]);
        assertThat(list).isNotNull();
        assertThat(list).isEmpty();
    }

    private static Converter<Object[], Collection<String>> createConverter() {
        Introspector introspector =
            ConverterIntrospectionHelper.createIntrospectorForCollection(Collection.class);
        ConverterFactory<Object[], Collection<?>> factory =
            new ArrayToCollectionConverterFactory(introspector);

        Converter<Object[], Collection<String>> converter =
            TypeUtils.unchecked(factory.create(Collection.class), Converter.class);
        assertThat(converter).isNotNull();

        return converter;
    }
}
