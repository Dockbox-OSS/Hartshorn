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
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Integer> converter = factory.create(Integer.class);
        final Integer converted = converter.convert("1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1, converted);
    }

    @Test
    void testValidStringToLong() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Long> converter = factory.create(Long.class);
        final Long converted = converter.convert("1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1L, converted);
    }

    @Test
    void testValidStringToFloat() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Float> converter = factory.create(Float.class);
        final Float converted = converter.convert("1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1.0F, converted);
    }

    @Test
    void testValidStringToDouble() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Double> converter = factory.create(Double.class);
        final Double converted = converter.convert("1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1.0D, converted);
    }

    @Test
    void testValidStringToShort() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Short> converter = factory.create(Short.class);
        final Short converted = converter.convert("1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals((short) 1, converted);
    }

    @Test
    void testValidStringToByte() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Byte> converter = factory.create(Byte.class);
        final Byte converted = converter.convert("1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals((byte) 1, converted);
    }

    @Test
    void testNegativeStringToInteger() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Integer> converter = factory.create(Integer.class);
        final Integer converted = converter.convert("-1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(-1, converted);
    }

    @Test
    void testNegativeStringToLong() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Long> converter = factory.create(Long.class);
        final Long converted = converter.convert("-1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(-1L, converted);
    }

    @Test
    void testNegativeStringToFloat() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Float> converter = factory.create(Float.class);
        final Float converted = converter.convert("-1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(-1.0F, converted);
    }

    @Test
    void testNegativeStringToDouble() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Double> converter = factory.create(Double.class);
        final Double converted = converter.convert("-1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(-1.0D, converted);
    }

    @Test
    void testNegativeStringToShort() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Short> converter = factory.create(Short.class);
        final Short converted = converter.convert("-1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals((short) -1, converted);
    }

    @Test
    void testNegativeStringToByte() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Byte> converter = factory.create(Byte.class);
        final Byte converted = converter.convert("-1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals((byte) -1, converted);
    }

    @Test
    void testInvalidStringToInteger() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Integer> converter = factory.create(Integer.class);
        final Integer converted = converter.convert("a");
        Assertions.assertNull(converted);
    }

    @Test
    void testInvalidStringToLong() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Long> converter = factory.create(Long.class);
        final Long converted = converter.convert("a");
        Assertions.assertNull(converted);
    }

    @Test
    void testInvalidStringToFloat() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Float> converter = factory.create(Float.class);
        final Float converted = converter.convert("a");
        Assertions.assertNull(converted);
    }

    @Test
    void testInvalidStringToDouble() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Double> converter = factory.create(Double.class);
        final Double converted = converter.convert("a");
        Assertions.assertNull(converted);
    }

    @Test
    void testInvalidStringToShort() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Short> converter = factory.create(Short.class);
        final Short converted = converter.convert("a");
        Assertions.assertNull(converted);
    }

    @Test
    void testInvalidStringToByte() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Byte> converter = factory.create(Byte.class);
        final Byte converted = converter.convert("a");
        Assertions.assertNull(converted);
    }

    @Test
    void testHexadecimalStringToInteger() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Integer> converter = factory.create(Integer.class);
        final Integer converted = converter.convert("0x1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1, converted);
    }

    @Test
    void testHexadecimalStringToLong() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Long> converter = factory.create(Long.class);
        final Long converted = converter.convert("0x1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1L, converted);
    }

    @Test
    void testHexadecimalStringToFloat() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Float> converter = factory.create(Float.class);
        final Float converted = converter.convert("0x1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1.0F, converted);
    }

    @Test
    void testHexadecimalStringToDouble() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Double> converter = factory.create(Double.class);
        final Double converted = converter.convert("0x1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1.0D, converted);
    }

    @Test
    void testHexadecimalStringToShort() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Short> converter = factory.create(Short.class);
        final Short converted = converter.convert("0x1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals((short) 1, converted);
    }

    @Test
    void testHexadecimalStringToByte() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Byte> converter = factory.create(Byte.class);
        final Byte converted = converter.convert("0x1");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals((byte) 1, converted);
    }

    @Test
    void testOctalStringToInteger() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Integer> converter = factory.create(Integer.class);
        final Integer converted = converter.convert("01");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1, converted);
    }

    @Test
    void testOctalStringToLong() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Long> converter = factory.create(Long.class);
        final Long converted = converter.convert("01");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1L, converted);
    }

    @Test
    void testOctalStringToFloat() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Float> converter = factory.create(Float.class);
        final Float converted = converter.convert("01");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1.0F, converted);
    }

    @Test
    void testOctalStringToDouble() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Double> converter = factory.create(Double.class);
        final Double converted = converter.convert("01");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals(1.0D, converted);
    }

    @Test
    void testOctalStringToShort() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Short> converter = factory.create(Short.class);
        final Short converted = converter.convert("01");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals((short) 1, converted);
    }

    @Test
    void testOctalStringToByte() {
        final ConverterFactory<String, Number> factory = new StringToNumberConverterFactory();
        final Converter<String, Byte> converter = factory.create(Byte.class);
        final Byte converted = converter.convert("01");
        Assertions.assertNotNull(converted);
        Assertions.assertEquals((byte) 1, converted);
    }
}
