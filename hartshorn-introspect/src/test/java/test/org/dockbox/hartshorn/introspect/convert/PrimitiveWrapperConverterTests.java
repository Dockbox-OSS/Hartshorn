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
        final GenericConverter converter = new PrimitiveWrapperConverter();
        final Object converted = converter.convert(1, int.class, Integer.class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Integer);
        Assertions.assertEquals(1, converted);
    }

    @Test
    void testLongToWrapperCanConvert() {
        final GenericConverter converter = new PrimitiveWrapperConverter();
        final Object converted = converter.convert(1L, long.class, Long.class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Long);
        Assertions.assertEquals(1L, converted);
    }

    @Test
    void testDoubleToWrapperCanConvert() {
        final GenericConverter converter = new PrimitiveWrapperConverter();
        final Object converted = converter.convert(1D, double.class, Double.class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Double);
        Assertions.assertEquals(1D, converted);
    }

    @Test
    void testFloatToWrapperCanConvert() {
        final GenericConverter converter = new PrimitiveWrapperConverter();
        final Object converted = converter.convert(1F, float.class, Float.class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Float);
        Assertions.assertEquals(1F, converted);
    }

    @Test
    void testShortToWrapperCanConvert() {
        final GenericConverter converter = new PrimitiveWrapperConverter();
        final Object converted = converter.convert((short) 1, short.class, Short.class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Short);
        Assertions.assertEquals((short) 1, converted);
    }

    @Test
    void testByteToWrapperCanConvert() {
        final GenericConverter converter = new PrimitiveWrapperConverter();
        final Object converted = converter.convert((byte) 1, byte.class, Byte.class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Byte);
        Assertions.assertEquals((byte) 1, converted);
    }

    @Test
    void testBooleanToWrapperCanConvert() {
        final GenericConverter converter = new PrimitiveWrapperConverter();
        final Object converted = converter.convert(true, boolean.class, Boolean.class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Boolean);
        Assertions.assertEquals(true, converted);
    }

    @Test
    void testCharToWrapperCanConvert() {
        final GenericConverter converter = new PrimitiveWrapperConverter();
        final Object converted = converter.convert('a', char.class, Character.class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Character);
        Assertions.assertEquals('a', converted);
    }

    @Test
    void testWrapperToIntCanConvert() {
        final PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        Assertions.assertTrue(converter.canConvert(Integer.valueOf(1), int.class));

        final int converted = (int) converter.convert(Integer.valueOf(1), Integer.class, int.class);
        Assertions.assertEquals(1, converted);
    }

    @Test
    void testWrapperToLongCanConvert() {
        final PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        Assertions.assertTrue(converter.canConvert(Long.valueOf(1L), long.class));

        final long converted = (long) converter.convert(Long.valueOf(1L), Long.class, long.class);
        Assertions.assertEquals(1L, converted);
    }

    @Test
    void testWrapperToDoubleCanConvert() {
        final PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        Assertions.assertTrue(converter.canConvert(Double.valueOf(1D), double.class));

        final double converted = (double) converter.convert(Double.valueOf(1D), Double.class, double.class);
        Assertions.assertEquals(1D, converted);
    }

    @Test
    void testWrapperToFloatCanConvert() {
        final PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        Assertions.assertTrue(converter.canConvert(Float.valueOf(1F), float.class));

        final float converted = (float) converter.convert(Float.valueOf(1F), Float.class, float.class);
        Assertions.assertEquals(1F, converted);
    }

    @Test
    void testWrapperToShortCanConvert() {
        final PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        Assertions.assertTrue(converter.canConvert(Short.valueOf((short) 1), short.class));

        final short converted = (short) converter.convert(Short.valueOf((short) 1), Short.class, short.class);
        Assertions.assertEquals((short) 1, converted);
    }

    @Test
    void testWrapperToByteCanConvert() {
        final PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        Assertions.assertTrue(converter.canConvert(Byte.valueOf((byte) 1), byte.class));

        final byte converted = (byte) converter.convert(Byte.valueOf((byte) 1), Byte.class, byte.class);
        Assertions.assertEquals((byte) 1, converted);
    }

    @Test
    void testWrapperToBooleanCanConvert() {
        final PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        Assertions.assertTrue(converter.canConvert(Boolean.TRUE, boolean.class));

        final boolean converted = (boolean) converter.convert(Boolean.TRUE, Boolean.class, boolean.class);
        Assertions.assertEquals(true, converted);
    }

    @Test
    void testWrapperToCharCanConvert() {
        final PrimitiveWrapperConverter converter = new PrimitiveWrapperConverter();
        Assertions.assertTrue(converter.canConvert(Character.valueOf('a'), char.class));

        final char converted = (char) converter.convert(Character.valueOf('a'), Character.class, char.class);
        Assertions.assertEquals('a', converted);
    }
}
