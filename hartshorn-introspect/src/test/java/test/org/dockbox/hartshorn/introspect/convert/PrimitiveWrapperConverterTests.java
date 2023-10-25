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

import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.PrimitiveWrapperConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PrimitiveWrapperConverterTests {

    @Test
    void testIntToWrapperCanConvert() {
        GenericConverter converter = new PrimitiveWrapperConverter();
        Object converted = converter.convert(1, int.class, Integer.class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Integer);
        Assertions.assertEquals(1, converted);
    }

    @Test
    void testLongToWrapperCanConvert() {
        GenericConverter converter = new PrimitiveWrapperConverter();
        Object converted = converter.convert(1L, long.class, Long.class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Long);
        Assertions.assertEquals(1L, converted);
    }

    @Test
    void testDoubleToWrapperCanConvert() {
        GenericConverter converter = new PrimitiveWrapperConverter();
        Object converted = converter.convert(1.0D, double.class, Double.class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Double);
        Assertions.assertEquals(1.0D, converted);
    }

    @Test
    void testFloatToWrapperCanConvert() {
        GenericConverter converter = new PrimitiveWrapperConverter();
        Object converted = converter.convert(1.0F, float.class, Float.class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Float);
        Assertions.assertEquals(1.0F, converted);
    }

    @Test
    void testShortToWrapperCanConvert() {
        GenericConverter converter = new PrimitiveWrapperConverter();
        Object converted = converter.convert((short) 1, short.class, Short.class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Short);
        Assertions.assertEquals((short) 1, converted);
    }

    @Test
    void testByteToWrapperCanConvert() {
        GenericConverter converter = new PrimitiveWrapperConverter();
        Object converted = converter.convert((byte) 1, byte.class, Byte.class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Byte);
        Assertions.assertEquals((byte) 1, converted);
    }

    @Test
    void testBooleanToWrapperCanConvert() {
        GenericConverter converter = new PrimitiveWrapperConverter();
        Object converted = converter.convert(true, boolean.class, Boolean.class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Boolean);
        Assertions.assertEquals(true, converted);
    }

    @Test
    void testCharToWrapperCanConvert() {
        GenericConverter converter = new PrimitiveWrapperConverter();
        Object converted = converter.convert('a', char.class, Character.class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Character);
        Assertions.assertEquals('a', converted);
    }

    @Test
    void testWrapperToIntCanConvert() {
        PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        Assertions.assertTrue(converter.canConvert(Integer.valueOf(1), int.class));

        int converted = (int) converter.convert(Integer.valueOf(1), Integer.class, int.class);
        Assertions.assertEquals(1, converted);
    }

    @Test
    void testWrapperToLongCanConvert() {
        PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        Assertions.assertTrue(converter.canConvert(Long.valueOf(1L), long.class));

        long converted = (long) converter.convert(Long.valueOf(1L), Long.class, long.class);
        Assertions.assertEquals(1L, converted);
    }

    @Test
    void testWrapperToDoubleCanConvert() {
        PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        Assertions.assertTrue(converter.canConvert(Double.valueOf(1.0D), double.class));

        double converted = (double) converter.convert(Double.valueOf(1.0D), Double.class, double.class);
        Assertions.assertEquals(1.0D, converted);
    }

    @Test
    void testWrapperToFloatCanConvert() {
        PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        Assertions.assertTrue(converter.canConvert(Float.valueOf(1.0F), float.class));

        float converted = (float) converter.convert(Float.valueOf(1.0F), Float.class, float.class);
        Assertions.assertEquals(1.0F, converted);
    }

    @Test
    void testWrapperToShortCanConvert() {
        PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        Assertions.assertTrue(converter.canConvert(Short.valueOf((short) 1), short.class));

        short converted = (short) converter.convert(Short.valueOf((short) 1), Short.class, short.class);
        Assertions.assertEquals((short) 1, converted);
    }

    @Test
    void testWrapperToByteCanConvert() {
        PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        Assertions.assertTrue(converter.canConvert(Byte.valueOf((byte) 1), byte.class));

        byte converted = (byte) converter.convert(Byte.valueOf((byte) 1), Byte.class, byte.class);
        Assertions.assertEquals((byte) 1, converted);
    }

    @Test
    void testWrapperToBooleanCanConvert() {
        PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        Assertions.assertTrue(converter.canConvert(Boolean.TRUE, boolean.class));

        boolean converted = (boolean) converter.convert(Boolean.TRUE, Boolean.class, boolean.class);
        Assertions.assertTrue(converted);
    }

    @Test
    void testWrapperToCharCanConvert() {
        PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        Assertions.assertTrue(converter.canConvert(Character.valueOf('a'), char.class));

        char converted = (char) converter.convert(Character.valueOf('a'), Character.class, char.class);
        Assertions.assertEquals('a', converted);
    }
}
