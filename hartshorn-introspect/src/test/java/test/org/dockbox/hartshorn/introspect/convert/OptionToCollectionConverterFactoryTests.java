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
import org.dockbox.hartshorn.util.introspect.convert.support.OptionToCollectionConverterFactory;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

public class OptionToCollectionConverterFactoryTests {

    @Test
    void testEmptyOptionalConvertsToEmptyCollection() {
        final Converter<Option<?>, ArrayList> converter = createConverter();
        final Option<String> option = Option.empty();

        final Collection<?> converted = converter.convert(option);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted.isEmpty());
    }

    @Test
    void testPresentOptionalConvertsToCollectionWithElement() {
        final Converter<Option<?>, ArrayList> converter = createConverter();
        final Option<String> option = Option.of("test");

        final Collection<?> converted = converter.convert(option);
        Assertions.assertNotNull(converted);
        Assertions.assertFalse(converted.isEmpty());
        Assertions.assertEquals(1, converted.size());
        Assertions.assertEquals("test", converted.iterator().next());
    }

    @Test
    void testSuccessSomeConvertsToCollectionWithElement() {
        final Converter<Option<?>, ArrayList> converter = createConverter();
        final Option<String> option = Attempt.of("test");

        final Collection<?> converted = converter.convert(option);
        Assertions.assertNotNull(converted);
        Assertions.assertFalse(converted.isEmpty());
        Assertions.assertEquals(1, converted.size());
        Assertions.assertEquals("test", converted.iterator().next());
    }

    @Test
    void testFailureSomeConvertsToCollectionWithElement() {
        final Converter<Option<?>, ArrayList> converter = createConverter();
        final Option<String> option = Attempt.of("test", new Exception());

        final Collection<?> converted = converter.convert(option);
        Assertions.assertNotNull(converted);
        Assertions.assertFalse(converted.isEmpty());
        Assertions.assertEquals(1, converted.size());
        Assertions.assertEquals("test", converted.iterator().next());
    }

    @Test
    void testFailureNoneConvertsToEmptyCollection() {
        final Converter<Option<?>, ArrayList> converter = createConverter();
        final Option<String> option = Attempt.of(new Exception());

        final Collection<?> converted = converter.convert(option);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted.isEmpty());
    }

    @Test
    void testSuccessNoneConvertsToEmptyCollection() {
        final Converter<Option<?>, ArrayList> converter = createConverter();
        final Option<String> option = Attempt.empty();

        final Collection<?> converted = converter.convert(option);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted.isEmpty());
    }

    private static Converter<Option<?>, ArrayList> createConverter() {
        final Introspector introspector = ConverterIntrospectionHelper.createIntrospectorForCollection(ArrayList.class, ArrayList::new);
        final ConverterFactory<Option<?>, Collection<?>> factory = new OptionToCollectionConverterFactory(introspector);
        return factory.create(ArrayList.class);
    }
}
