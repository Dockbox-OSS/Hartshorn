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
import org.dockbox.hartshorn.util.introspect.convert.support.ObjectToArrayConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ObjectToArrayConverterTests {

    @Test
    void testNonNullElementCanBeConverted() {
        String element = "test";
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, Object.class, Object[].class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Object[]);
        Assertions.assertEquals(1, ((Object[]) converted).length);
        Assertions.assertEquals(element, ((Object[]) converted)[0]);
    }

    @Test
    void testElementTypeIsRetained() {
        String element = "test";
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, String.class, String[].class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof String[]);
        Assertions.assertEquals(1, ((String[]) converted).length);
        Assertions.assertEquals(element, ((String[]) converted)[0]);
    }

    @Test
    void testNullElementCanBeConverted() {
        Object element = null;
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, Object.class, Object[].class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof Object[]);
        Assertions.assertEquals(1, ((Object[]) converted).length);
        Assertions.assertNull(((Object[]) converted)[0]);
    }

    @Test
    void testPrimitiveIntCanBeConverted() {
        int element = 1;
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, int.class, int[].class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof int[]);
        Assertions.assertEquals(1, ((int[]) converted).length);
        Assertions.assertEquals(element, ((int[]) converted)[0]);
    }

    @Test
    void testPrimitiveLongCanBeConverted() {
        long element = 1L;
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, long.class, long[].class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof long[]);
        Assertions.assertEquals(1, ((long[]) converted).length);
        Assertions.assertEquals(element, ((long[]) converted)[0]);
    }

    @Test
    void testPrimitiveShortCanBeConverted() {
        short element = 1;
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, short.class, short[].class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof short[]);
        Assertions.assertEquals(1, ((short[]) converted).length);
        Assertions.assertEquals(element, ((short[]) converted)[0]);
    }

    @Test
    void testPrimitiveByteCanBeConverted() {
        byte element = 1;
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, byte.class, byte[].class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof byte[]);
        Assertions.assertEquals(1, ((byte[]) converted).length);
        Assertions.assertEquals(element, ((byte[]) converted)[0]);
    }

    @Test
    void testPrimitiveFloatCanBeConverted() {
        float element = 1.0F;
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, float.class, float[].class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof float[]);
        Assertions.assertEquals(1, ((float[]) converted).length);
        Assertions.assertEquals(element, ((float[]) converted)[0]);
    }

    @Test
    void testPrimitiveDoubleCanBeConverted() {
        double element = 1.0D;
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, double.class, double[].class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof double[]);
        Assertions.assertEquals(1, ((double[]) converted).length);
        Assertions.assertEquals(element, ((double[]) converted)[0]);
    }

    @Test
    void testPrimitiveCharCanBeConverted() {
        char element = 'a';
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, char.class, char[].class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof char[]);
        Assertions.assertEquals(1, ((char[]) converted).length);
        Assertions.assertEquals(element, ((char[]) converted)[0]);
    }

    @Test
    void testPrimitiveBooleanCanBeConverted() {
        boolean element = true;
        GenericConverter converter = new ObjectToArrayConverter();
        Object converted = converter.convert(element, boolean.class, boolean[].class);
        Assertions.assertNotNull(converted);
        Assertions.assertTrue(converted instanceof boolean[]);
        Assertions.assertEquals(1, ((boolean[]) converted).length);
        Assertions.assertEquals(element, ((boolean[]) converted)[0]);
    }
}
