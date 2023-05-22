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

package test.org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.util.BuiltInStringTypeAdapters;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BuiltInStringTypeAdaptersTests {

    @Test
    void testStringAdapterReturnsSame() {
        final Option<String> adapted = BuiltInStringTypeAdapters.STRING.adapt("test");
        Assertions.assertTrue(adapted.present());
        Assertions.assertEquals("test", adapted.get());
    }

    @Test
    void testCharacterAdapterReturnsFirstCharacterIfStringOneCharacter() {
        final Option<Character> adapted = BuiltInStringTypeAdapters.CHARACTER.adapt("t");
        Assertions.assertTrue(adapted.present());
        Assertions.assertEquals('t', adapted.get());
    }

    @Test
    void testCharacterAdapterReturnsEmptyIfStringNotOneCharacter() {
        final Option<Character> adapted = BuiltInStringTypeAdapters.CHARACTER.adapt("test");
        Assertions.assertFalse(adapted.present());
    }

    @Test
    void testCharacterAdapterReturnsEmptyIfStringEmpty() {
        final Option<Character> adapted = BuiltInStringTypeAdapters.CHARACTER.adapt("");
        Assertions.assertFalse(adapted.present());
    }

    @Test
    void testBooleanAdapterReturnsTrueIfStringIsTrue() {
        final Option<Boolean> adapted = BuiltInStringTypeAdapters.BOOLEAN.adapt("true");
        Assertions.assertTrue(adapted.present());
        Assertions.assertTrue(adapted.get());
    }

    @Test
    void testBooleanAdapterReturnsFalseIfStringIsFalse() {
        final Option<Boolean> adapted = BuiltInStringTypeAdapters.BOOLEAN.adapt("false");
        Assertions.assertTrue(adapted.present());
        Assertions.assertFalse(adapted.get());
    }

    @Test
    void testBooleanAdapterReturnsTrueIfStringIsYes() {
        final Option<Boolean> adapted = BuiltInStringTypeAdapters.BOOLEAN.adapt("yes");
        Assertions.assertTrue(adapted.present());
        Assertions.assertTrue(adapted.get());
    }

    @Test
    void testBooleanAdapterReturnsFalseIfStringIsNo() {
        final Option<Boolean> adapted = BuiltInStringTypeAdapters.BOOLEAN.adapt("no");
        Assertions.assertTrue(adapted.present());
        Assertions.assertFalse(adapted.get());
    }

    @Test
    void testBooleanAdapterReturnsFalseIfStringIsNotBoolean() {
        final Option<Boolean> adapted = BuiltInStringTypeAdapters.BOOLEAN.adapt("test");
        Assertions.assertTrue(adapted.present());
        Assertions.assertFalse(adapted.get());
    }

    @Test
    void testDoubleAdapterReturnsDoubleIfStringIsDouble() {
        final Option<Double> adapted = BuiltInStringTypeAdapters.DOUBLE.adapt("1.0");
        Assertions.assertTrue(adapted.present());
        Assertions.assertEquals(1.0, adapted.get());
    }

    @Test
    void testDoubleAdapterReturnsEmptyIfStringIsNotDouble() {
        final Option<Double> adapted = BuiltInStringTypeAdapters.DOUBLE.adapt("test");
        Assertions.assertFalse(adapted.present());
    }

    @Test
    void testFloatAdapterReturnsFloatIfStringIsFloat() {
        final Option<Float> adapted = BuiltInStringTypeAdapters.FLOAT.adapt("1.0");
        Assertions.assertTrue(adapted.present());
        Assertions.assertEquals(1.0f, adapted.get());
    }

    @Test
    void testFloatAdapterReturnsEmptyIfStringIsNotFloat() {
        final Option<Float> adapted = BuiltInStringTypeAdapters.FLOAT.adapt("test");
        Assertions.assertFalse(adapted.present());
    }

    @Test
    void testIntegerAdapterReturnsIntegerIfStringIsInteger() {
        final Option<Integer> adapted = BuiltInStringTypeAdapters.INTEGER.adapt("1");
        Assertions.assertTrue(adapted.present());
        Assertions.assertEquals(1, adapted.get());
    }

    @Test
    void testIntegerAdapterReturnsEmptyIfStringIsNotInteger() {
        final Option<Integer> adapted = BuiltInStringTypeAdapters.INTEGER.adapt("test");
        Assertions.assertFalse(adapted.present());
    }

    @Test
    void testLongAdapterReturnsLongIfStringIsLong() {
        final Option<Long> adapted = BuiltInStringTypeAdapters.LONG.adapt("1");
        Assertions.assertTrue(adapted.present());
        Assertions.assertEquals(1L, adapted.get());
    }

    @Test
    void testLongAdapterReturnsEmptyIfStringIsNotLong() {
        final Option<Long> adapted = BuiltInStringTypeAdapters.LONG.adapt("test");
        Assertions.assertFalse(adapted.present());
    }

    @Test
    void testShortAdapterReturnsShortIfStringIsShort() {
        final Option<Short> adapted = BuiltInStringTypeAdapters.SHORT.adapt("1");
        Assertions.assertTrue(adapted.present());
        Assertions.assertEquals(1, (short) adapted.get());
    }

    @Test
    void testShortAdapterReturnsEmptyIfStringIsNotShort() {
        final Option<Short> adapted = BuiltInStringTypeAdapters.SHORT.adapt("test");
        Assertions.assertFalse(adapted.present());
    }

    @Test
    void testUUIDAdapterReturnsUUIDIfStringIsUUID() {
        final Option<java.util.UUID> adapted = BuiltInStringTypeAdapters.UNIQUE_ID.adapt("00000000-0000-0000-0000-000000000000");
        Assertions.assertTrue(adapted.present());
        Assertions.assertEquals(java.util.UUID.fromString("00000000-0000-0000-0000-000000000000"), adapted.get());
    }

    @Test
    void testUUIDAdapterReturnsEmptyIfStringIsNotUUID() {
        final Option<java.util.UUID> adapted = BuiltInStringTypeAdapters.UNIQUE_ID.adapt("test");
        Assertions.assertFalse(adapted.present());
    }
}
