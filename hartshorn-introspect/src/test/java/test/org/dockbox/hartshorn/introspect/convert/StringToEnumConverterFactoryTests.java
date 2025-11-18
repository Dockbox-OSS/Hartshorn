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

import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToEnumConverterFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringToEnumConverterFactoryTests {

    @Test
    void matchingNameCanConvert() {
        Converter<String, Color> converter = createConverter();
        Color red = converter.convert("RED");
        assertThat(red).isNotNull();
        assertThat(red).isSameAs(Color.RED);
    }

    @Test
    void matchingNameIgnoreCaseCanNotConvert() {
        Converter<String, Color> converter = createConverter();
        Color red = converter.convert("red");
        assertThat(red).isNull();
    }

    @Test
    void nonMatchingNameCanNotConvert() {
        Converter<String, Color> converter = createConverter();
        Color yellow = converter.convert("YELLOW");
        assertThat(yellow).isNull();
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