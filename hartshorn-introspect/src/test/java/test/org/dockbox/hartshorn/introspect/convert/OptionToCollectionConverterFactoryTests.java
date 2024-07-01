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

import java.util.ArrayList;
import java.util.Collection;

import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.OptionToCollectionConverterFactory;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OptionToCollectionConverterFactoryTests {

    @Test
    void testEmptyOptionalConvertsToEmptyCollection() {
        Converter<Option<?>, ArrayList<String>> converter = createConverter();
        Option<String> option = Option.empty();

        Collection<?> converted = converter.convert(option);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted.isEmpty());
    }

    @Test
    void testPresentOptionalConvertsToCollectionWithElement() {
        Converter<Option<?>, ArrayList<String>> converter = createConverter();
        Option<String> option = Option.of("test");

        Collection<?> converted = converter.convert(option);
        Assertions.assertNotNull(converted);
        Assertions.assertFalse(converted.isEmpty());
        Assertions.assertEquals(1, converted.size());
        Assertions.assertEquals("test", CollectionUtilities.first(converted));
    }

    @SuppressWarnings("NonApiType")
    private static Converter<Option<?>, ArrayList<String>> createConverter() {
        Introspector introspector = ConverterIntrospectionHelper.createIntrospectorForCollection(ArrayList.class, ArrayList::new);
        ConverterFactory<Option<?>, Collection<?>> factory = new OptionToCollectionConverterFactory(introspector);
        return TypeUtils.adjustWildcards(factory.create(ArrayList.class), Converter.class);
    }
}
