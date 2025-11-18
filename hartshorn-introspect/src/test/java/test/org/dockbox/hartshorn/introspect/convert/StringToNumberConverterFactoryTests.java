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
import org.dockbox.hartshorn.util.introspect.convert.support.StringToNumberConverterFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringToNumberConverterFactoryTests {

    @Test
    void validStringToInteger() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Integer> converter = factory.create(Integer.class);
        Integer converted = converter.convert("1");
        assertThat(converted)
                .isNotNull()
                .isOne();
    }

    @Test
    void validStringToLong() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Long> converter = factory.create(Long.class);
        Long converted = converter.convert("1");
        assertThat(converted)
                .isNotNull()
                .isOne();
    }

    @Test
    void validStringToFloat() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Float> converter = factory.create(Float.class);
        Float converted = converter.convert("1");
        assertThat(converted)
                .isNotNull()
                .isOne();
    }

    @Test
    void validStringToDouble() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Double> converter = factory.create(Double.class);
        Double converted = converter.convert("1");
        assertThat(converted)
                .isNotNull()
                .isOne();
    }

    @Test
    void validStringToShort() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Short> converter = factory.create(Short.class);
        Short converted = converter.convert("1");
        assertThat(converted)
                .isNotNull()
                .isOne();
    }

    @Test
    void validStringToByte() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Byte> converter = factory.create(Byte.class);
        Byte converted = converter.convert("1");
        assertThat(converted)
                .isNotNull()
                .isOne();
    }

    @Test
    void negativeStringToInteger() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Integer> converter = factory.create(Integer.class);
        Integer converted = converter.convert("-1");
        assertThat(converted)
                .isNotNull()
                .isEqualTo(-1);
    }

    @Test
    void negativeStringToLong() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Long> converter = factory.create(Long.class);
        Long converted = converter.convert("-1");
        assertThat(converted)
                .isNotNull()
                .isEqualTo(-1L);
    }

    @Test
    void negativeStringToFloat() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Float> converter = factory.create(Float.class);
        Float converted = converter.convert("-1");
        assertThat(converted)
                .isNotNull()
                .isEqualTo(-1.0F);
    }

    @Test
    void negativeStringToDouble() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Double> converter = factory.create(Double.class);
        Double converted = converter.convert("-1");
        assertThat(converted)
                .isNotNull()
                .isEqualTo(-1.0D);
    }

    @Test
    void negativeStringToShort() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Short> converter = factory.create(Short.class);
        Short converted = converter.convert("-1");
        assertThat(converted)
                .isNotNull()
                .isEqualTo((short) -1);
    }

    @Test
    void negativeStringToByte() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Byte> converter = factory.create(Byte.class);
        Byte converted = converter.convert("-1");
        assertThat(converted)
                .isNotNull()
                .isEqualTo((byte) -1);
    }

    @Test
    void invalidStringToInteger() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Integer> converter = factory.create(Integer.class);
        Integer converted = converter.convert("a");
        assertThat(converted).isNull();
    }

    @Test
    void invalidStringToLong() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Long> converter = factory.create(Long.class);
        Long converted = converter.convert("a");
        assertThat(converted).isNull();
    }

    @Test
    void invalidStringToFloat() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Float> converter = factory.create(Float.class);
        Float converted = converter.convert("a");
        assertThat(converted).isNull();
    }

    @Test
    void invalidStringToDouble() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Double> converter = factory.create(Double.class);
        Double converted = converter.convert("a");
        assertThat(converted).isNull();
    }

    @Test
    void invalidStringToShort() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Short> converter = factory.create(Short.class);
        Short converted = converter.convert("a");
        assertThat(converted).isNull();
    }

    @Test
    void invalidStringToByte() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Byte> converter = factory.create(Byte.class);
        Byte converted = converter.convert("a");
        assertThat(converted).isNull();
    }

    @Test
    void hexadecimalStringToInteger() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Integer> converter = factory.create(Integer.class);
        Integer converted = converter.convert("0x1");
        assertThat(converted)
                .isNotNull()
                .isOne();
    }

    @Test
    void hexadecimalStringToLong() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Long> converter = factory.create(Long.class);
        Long converted = converter.convert("0x1");
        assertThat(converted)
                .isNotNull()
                .isOne();
    }

    @Test
    void hexadecimalStringToFloat() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Float> converter = factory.create(Float.class);
        Float converted = converter.convert("0x1");
        assertThat(converted)
                .isNotNull()
                .isOne();
    }

    @Test
    void hexadecimalStringToDouble() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Double> converter = factory.create(Double.class);
        Double converted = converter.convert("0x1");
        assertThat(converted)
                .isNotNull()
                .isOne();
    }

    @Test
    void hexadecimalStringToShort() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Short> converter = factory.create(Short.class);
        Short converted = converter.convert("0x1");
        assertThat(converted)
                .isNotNull()
                .isOne();
    }

    @Test
    void hexadecimalStringToByte() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Byte> converter = factory.create(Byte.class);
        Byte converted = converter.convert("0x1");
        assertThat(converted)
                .isNotNull()
                .isOne();
    }

    @Test
    void octalStringToInteger() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Integer> converter = factory.create(Integer.class);
        Integer converted = converter.convert("01");
        assertThat(converted)
                .isNotNull()
                .isOne();
    }

    @Test
    void octalStringToLong() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Long> converter = factory.create(Long.class);
        Long converted = converter.convert("01");
        assertThat(converted)
                .isNotNull()
                .isOne();
    }

    @Test
    void octalStringToFloat() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Float> converter = factory.create(Float.class);
        Float converted = converter.convert("01");
        assertThat(converted)
                .isNotNull()
                .isOne();
    }

    @Test
    void octalStringToDouble() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Double> converter = factory.create(Double.class);
        Double converted = converter.convert("01");
        assertThat(converted)
                .isNotNull()
                .isOne();
    }

    @Test
    void octalStringToShort() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Short> converter = factory.create(Short.class);
        Short converted = converter.convert("01");
        assertThat(converted)
                .isNotNull()
                .isOne();
    }

    @Test
    void octalStringToByte() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Byte> converter = factory.create(Byte.class);
        Byte converted = converter.convert("01");
        assertThat(converted)
                .isNotNull()
                .isOne();
    }
}
