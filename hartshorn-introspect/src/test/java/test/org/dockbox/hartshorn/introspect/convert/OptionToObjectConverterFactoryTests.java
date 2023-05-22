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

import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.OptionToObjectConverterFactory;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OptionToObjectConverterFactoryTests {

    @Test
    void testPresentOptionConvertsToObject() {
        final ConverterFactory<Option<?>, Object> factory = new OptionToObjectConverterFactory();
        final Converter<Option<?>, String> converter = factory.create(String.class);
        final Option<String> option = Option.of("test");
        final String converted = converter.convert(option);
        Assertions.assertNotNull(converted);
        Assertions.assertEquals("test", converted);
    }

    @Test
    void testEmptyOptionConvertsToNull() {
        final ConverterFactory<Option<?>, Object> factory = new OptionToObjectConverterFactory();
        final Converter<Option<?>, String> converter = factory.create(String.class);
        final Option<String> option = Option.empty();
        final String converted = converter.convert(option);
        Assertions.assertNull(converted);
    }
}
