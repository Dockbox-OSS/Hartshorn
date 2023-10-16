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

package test.org.dockbox.hartshorn.introspect.convert;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.ArrayToCollectionConverterFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("rawtypes")
public class ArrayToCollectionConverterFactoryTests {

    @Test
    void testFactoryCreatesConverterForConcreteCollectionTarget() {
        Introspector introspector = ConverterIntrospectionHelper.createIntrospectorForCollection(ArrayList.class, ArrayList::new);

        ConverterFactory<Object[], Collection<?>> factory = new ArrayToCollectionConverterFactory(introspector);
        Converter<Object[], ArrayList> converter = factory.create(ArrayList.class);
        Assertions.assertNotNull(converter);

        List<?> list = converter.convert(new Object[]{ "test" });
        Assertions.assertNotNull(list);
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("test", list.get(0));
    }

    @Test
    void testFactoryCreatesConverterForInterfaceCollectionTarget() {
        Converter<Object[], Collection> converter = createConverter();
        Assertions.assertNotNull(converter);

        Collection list = converter.convert(new Object[]{ "test" });
        Assertions.assertNotNull(list);
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("test", list.iterator().next());
    }

    @Test
    void testFactoryCreatesEmptyCollectionIfArrayEmpty() {
        Converter<Object[], Collection> converter = createConverter();

        Collection list = converter.convert(new Object[0]);
        Assertions.assertNotNull(list);
        Assertions.assertEquals(0, list.size());
    }

    private static Converter<Object[], Collection> createConverter() {
        Introspector introspector = ConverterIntrospectionHelper.createIntrospectorForCollection(Collection.class);
        ConverterFactory<Object[], Collection<?>> factory = new ArrayToCollectionConverterFactory(introspector);

        Converter<Object[], Collection> converter = factory.create(Collection.class);
        Assertions.assertNotNull(converter);

        return converter;
    }
}
