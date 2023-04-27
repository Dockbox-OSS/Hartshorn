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
import org.dockbox.hartshorn.util.introspect.convert.support.OptionalToOptionConverter;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class OptionalToOptionConverterTests {

    @Test
    void testPresentOptionalConvertsToPresentOption() {
        final Converter<Optional<?>, Option<?>> converter = new OptionalToOptionConverter();
        final Optional<String> option = Optional.of("test");
        final Option<?> converted = converter.convert(option);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted.present());
        Assertions.assertEquals("test", converted.get());
    }

    @Test
    void testEmptyOptionalConvertsToEmptyOption() {
        final Converter<Optional<?>, Option<?>> converter = new OptionalToOptionConverter();
        final Optional<String> option = Optional.empty();
        final Option<?> converted = converter.convert(option);
        Assertions.assertNotNull(converted);
        Assertions.assertFalse(converted.present());
    }
}