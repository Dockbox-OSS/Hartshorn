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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringToNumberConverterFactoryTests {

    @Test
    void testValidStringToInteger() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Integer> converter = factory.create(Integer.class);
        Integer converted = converter.convert("1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1, converted);
    }

    @Test
    void testValidStringToLong() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Long> converter = factory.create(Long.class);
        Long converted = converter.convert("1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1L, converted);
    }

    @Test
    void testValidStringToFloat() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Float> converter = factory.create(Float.class);
        Float converted = converter.convert("1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1.0F, converted);
    }

    @Test
    void testValidStringToDouble() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Double> converter = factory.create(Double.class);
        Double converted = converter.convert("1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1.0D, converted);
    }

    @Test
    void testValidStringToShort() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Short> converter = factory.create(Short.class);
        Short converted = converter.convert("1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals((short) 1, converted);
    }

    @Test
    void testValidStringToByte() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Byte> converter = factory.create(Byte.class);
        Byte converted = converter.convert("1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals((byte) 1, converted);
    }

    @Test
    void testNegativeStringToInteger() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Integer> converter = factory.create(Integer.class);
        Integer converted = converter.convert("-1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(-1, converted);
    }

    @Test
    void testNegativeStringToLong() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Long> converter = factory.create(Long.class);
        Long converted = converter.convert("-1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(-1L, converted);
    }

    @Test
    void testNegativeStringToFloat() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Float> converter = factory.create(Float.class);
        Float converted = converter.convert("-1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(-1.0F, converted);
    }

    @Test
    void testNegativeStringToDouble() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Double> converter = factory.create(Double.class);
        Double converted = converter.convert("-1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(-1.0D, converted);
    }

    @Test
    void testNegativeStringToShort() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Short> converter = factory.create(Short.class);
        Short converted = converter.convert("-1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals((short) -1, converted);
    }

    @Test
    void testNegativeStringToByte() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Byte> converter = factory.create(Byte.class);
        Byte converted = converter.convert("-1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals((byte) -1, converted);
    }

    @Test
    void testInvalidStringToInteger() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Integer> converter = factory.create(Integer.class);
        Integer converted = converter.convert("a");
        Assertions.assertNull(converted);
    }

    @Test
    void testInvalidStringToLong() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Long> converter = factory.create(Long.class);
        Long converted = converter.convert("a");
        Assertions.assertNull(converted);
    }

    @Test
    void testInvalidStringToFloat() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Float> converter = factory.create(Float.class);
        Float converted = converter.convert("a");
        Assertions.assertNull(converted);
    }

    @Test
    void testInvalidStringToDouble() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Double> converter = factory.create(Double.class);
        Double converted = converter.convert("a");
        Assertions.assertNull(converted);
    }

    @Test
    void testInvalidStringToShort() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Short> converter = factory.create(Short.class);
        Short converted = converter.convert("a");
        Assertions.assertNull(converted);
    }

    @Test
    void testInvalidStringToByte() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Byte> converter = factory.create(Byte.class);
        Byte converted = converter.convert("a");
        Assertions.assertNull(converted);
    }

    @Test
    void testHexadecimalStringToInteger() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Integer> converter = factory.create(Integer.class);
        Integer converted = converter.convert("0x1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1, converted);
    }

    @Test
    void testHexadecimalStringToLong() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Long> converter = factory.create(Long.class);
        Long converted = converter.convert("0x1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1L, converted);
    }

    @Test
    void testHexadecimalStringToFloat() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Float> converter = factory.create(Float.class);
        Float converted = converter.convert("0x1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1.0F, converted);
    }

    @Test
    void testHexadecimalStringToDouble() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Double> converter = factory.create(Double.class);
        Double converted = converter.convert("0x1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1.0D, converted);
    }

    @Test
    void testHexadecimalStringToShort() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Short> converter = factory.create(Short.class);
        Short converted = converter.convert("0x1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals((short) 1, converted);
    }

    @Test
    void testHexadecimalStringToByte() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Byte> converter = factory.create(Byte.class);
        Byte converted = converter.convert("0x1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals((byte) 1, converted);
    }

    @Test
    void testOctalStringToInteger() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Integer> converter = factory.create(Integer.class);
        Integer converted = converter.convert("01");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1, converted);
    }

    @Test
    void testOctalStringToLong() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Long> converter = factory.create(Long.class);
        Long converted = converter.convert("01");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1L, converted);
    }

    @Test
    void testOctalStringToFloat() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Float> converter = factory.create(Float.class);
        Float converted = converter.convert("01");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1.0F, converted);
    }

    @Test
    void testOctalStringToDouble() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Double> converter = factory.create(Double.class);
        Double converted = converter.convert("01");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1.0D, converted);
    }

    @Test
    void testOctalStringToShort() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Short> converter = factory.create(Short.class);
        Short converted = converter.convert("01");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals((short) 1, converted);
    }

    @Test
    void testOctalStringToByte() {
        ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        Converter<String, Byte> converter = factory.create(Byte.class);
        Byte converted = converter.convert("01");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals((byte) 1, converted);
    }
}
