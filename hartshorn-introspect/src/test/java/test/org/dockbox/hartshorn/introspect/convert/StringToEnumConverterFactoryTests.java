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
import org.dockbox.hartshorn.util.introspect.convert.support.StringToEnumConverterFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringToEnumConverterFactoryTests {

    @Test
    void testMatchingNameCanConvert() {
        Converter<String, Color> converter = createConverter();
        Color red = converter.convert("RED");
        Assertions.assertNotNull(red);
        Assertions.assertSame(Color.RED, red);
    }

    @Test
    void testMatchingNameIgnoreCaseCanNotConvert() {
        Converter<String, Color> converter = createConverter();
        Color red = converter.convert("red");
        Assertions.assertNull(red);
    }

    @Test
    void testNonMatchingNameCanNotConvert() {
        Converter<String, Color> converter = createConverter();
        Color yellow = converter.convert("YELLOW");
        Assertions.assertNull(yellow);
    }

    private static Converter<String, Color> createConverter() {
        ConverterFactory<String, Enum> factory = new StringToEnumConverterFactory();
        return factory.create(Color.class);
    }

    enum Color {
        RED,
        GREEN,
        BLUE,
    }
}