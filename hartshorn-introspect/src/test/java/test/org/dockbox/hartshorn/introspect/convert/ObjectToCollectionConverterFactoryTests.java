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
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToCollectionConverterFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Set;

@SuppressWarnings("rawtypes")
public class ObjectToCollectionConverterFactoryTests {

    @Test
    void testNonNullElementCanBeConverted() {
        final String element = "test";
        final Converter<Object, Set> converter = createConverter();
        final Object converted = converter.convert(element);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Set);
        Assertions.assertEquals(1, ((Collection) converted).size());
        Assertions.assertEquals(element, ((Iterable) converted).iterator().next());
    }

    @Test
    void testPrimitiveCanBeConverted() {
        final int element = 1;
        final Converter<Object, Set> converter = createConverter();
        final Object converted = converter.convert(element);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Set);
        Assertions.assertEquals(1, ((Collection) converted).size());
        Assertions.assertEquals(element, ((Iterable) converted).iterator().next());
    }

    private static Converter<Object, Set> createConverter() {
        final Introspector introspector = ConverterIntrospectionHelper.createIntrospectorForCollection(Set.class);
        final ConverterFactory<Object, Collection<?>> factory = new ObjectToCollectionConverterFactory(introspector);
        return factory.create(Set.class);
    }
}
