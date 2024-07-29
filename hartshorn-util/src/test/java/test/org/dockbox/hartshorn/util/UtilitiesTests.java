/*
 * Copyright 2019-2024 the original author or authors.
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

import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.NotPrimitiveException;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.TypeConversionException;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class UtilitiesTests {

    private static final int minute = 60;
    private static final int hour = 60 * minute;
    private static final int day = 24 * hour;
    private static final int week = 7 * day;

    private static Stream<Arguments> capitalizeValues() {
        return Stream.of(
                Arguments.of("value", "Value"),
                Arguments.of("Value", "Value"),
                Arguments.of("VALUE", "VALUE"),
                Arguments.of(" value", " value")
        );
    }

    private static Stream<Arguments> durations() {
        return Stream.of(
                Arguments.of("1s", 1),
                Arguments.of("1m", minute),
                Arguments.of("1h", hour),
                Arguments.of("1d", day),
                Arguments.of("1w", week),
                Arguments.of("1w1d1h1m1s", week + day + hour + minute + 1),
                Arguments.of("2w3d", (2 * week) + (3 * day)),
                Arguments.of("2w3d5h", (2 * week) + (3 * day) + (5 * hour)),
                Arguments.of("17h21m13s", (17 * hour) + (21 * minute) + 13)
        );
    }

    public static Stream<Arguments> differences() {
        return Stream.of(
                Arguments.of(List.of("a", "b"), List.of("a"), List.of("b")),
                Arguments.of(List.of("a"), List.of("a", "b"), List.of("b"))
        );
    }

    public static Stream<Arguments> stringFormats() {
        return Stream.of(
                Arguments.of("Hello world!", "Hello world!", new Object[0]),
                Arguments.of("Hello %s!", "Hello %s!", new Object[]{ "world" }),
                Arguments.of("Hello {0}!", "Hello world!", new Object[]{ "world" }),
                Arguments.of("{0} {1}!", "Hello world!", new Object[]{ "Hello", "world" }),
                Arguments.of("{0} {0}!", "Hello Hello!", new Object[]{ "Hello", "world" })
        );
    }

    public static Stream<Arguments> stringMapFormats() {
        return Stream.of(
                Arguments.of("Hello world!", "Hello world!", Map.of()),
                Arguments.of("Hello %s!", "Hello %s!", Map.of("{0}", "world")),
                Arguments.of("Hello {0}!", "Hello world!", Map.of("{0}", "world")),
                Arguments.of("{0} {1}!", "Hello world!", Map.of("{0}", "Hello", "{1}", "world")),
                Arguments.of("{0} {0}!", "Hello Hello!", Map.of("{0}", "Hello", "{1}", "world")),
                Arguments.of("Hello world!", "Hello user!", Map.of("world", "user"))
        );
    }

    @ParameterizedTest
    @MethodSource("capitalizeValues")
    void testCapitalizeChangesOnlyFirstCharacter(String input, String expected) {
        String value = StringUtilities.capitalize(input);
        Assertions.assertNotNull(value);
        Assertions.assertEquals(expected, value);
    }

    @Test
    void testIsEmptyStringTrueIfNull() {
        Assertions.assertTrue(StringUtilities.empty(null));
    }

    @Test
    void testIsEmptyStringTrueIfEmpty() {
        Assertions.assertTrue(StringUtilities.empty(""));
    }

    @Test
    void testIsEmptyStringFalseIfContent() {
        Assertions.assertFalse(StringUtilities.empty("value"));
    }

    @Test
    void testIsNotEmptyStringFalseIfNull() {
        Assertions.assertFalse(StringUtilities.notEmpty(null));
    }

    @Test
    void testIsNotEmptyStringFalseIfEmpty() {
        Assertions.assertFalse(StringUtilities.notEmpty(""));
    }

    @Test
    void testIsNotEmptyStringTrueIfContent() {
        Assertions.assertTrue(StringUtilities.notEmpty("value"));
    }

    @Test
    void testStripReplacesAllSpaces() {
        String stripped = StringUtilities.strip(" val ue  ");
        Assertions.assertNotNull(stripped);
        Assertions.assertEquals("value", stripped);
    }

    @Test
    void testStripReplacesAllTabs() {
        String stripped = StringUtilities.strip("\tval\tue\t\t");
        Assertions.assertNotNull(stripped);
        Assertions.assertEquals("value", stripped);
    }

    @Test
    void testStripReplacesAllNewLines() {
        String stripped = StringUtilities.strip("\nval\nue\n\n");
        Assertions.assertNotNull(stripped);
        Assertions.assertEquals("value", stripped);
    }

    @Test
    void testStripReplacesAllCarriageReturns() {
        String stripped = StringUtilities.strip("\rval\rue\r\r");
        Assertions.assertNotNull(stripped);
        Assertions.assertEquals("value", stripped);
    }

    @Test
    void testTrimWithSpaces() {
        String trimmed = StringUtilities.trimWith(' ', " value  ");
        Assertions.assertNotNull(trimmed);
        Assertions.assertEquals("value", trimmed);
    }

    @Test
    void testTrimWithRegExCharacter() {
        String trimmed = StringUtilities.trimWith('$', "$value$$");
        Assertions.assertNotNull(trimmed);
        Assertions.assertEquals("value", trimmed);
    }

    @Test
    void testCollectionMerge() {
        Collection<Integer> col1 = Arrays.asList(1, 2, 3);
        Collection<Integer> col2 = Arrays.asList(4, 5, 6);
        Collection<Integer> merged = CollectionUtilities.merge(col1, col2);

        Assertions.assertEquals(6, merged.size());
        Assertions.assertTrue(merged.containsAll(Arrays.asList(1, 2, 3, 4, 5, 6)));
    }

    @ParameterizedTest
    @MethodSource("durations")
    void testDurationOf(String in, long expected) {
        Option<Duration> duration = StringUtilities.durationOf(in);
        Assertions.assertTrue(duration.present());
        Assertions.assertEquals(expected, duration.get().getSeconds());
    }

    @ParameterizedTest
    @MethodSource("differences")
    void testDifferenceInCollections(Collection<String> collectionOne, Collection<String> collectionTwo, Collection<String> expected) {
        Set<String> difference = CollectionUtilities.difference(collectionOne, collectionTwo);
        Assertions.assertEquals(difference.size(), expected.size());
        Assertions.assertTrue(difference.containsAll(expected));
        Assertions.assertTrue(expected.containsAll(difference));
    }

    @Test
    void testSplitCapitals() {
        String input = "ThisIsAString";
        String[] expected = { "This", "Is", "A", "String" };
        String[] actual = StringUtilities.splitCapitals(input);
        Assertions.assertArrayEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("stringFormats")
    void testFormat(String format, String expected, Object... args) {
        String actual = StringUtilities.format(format, args);
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("stringMapFormats")
    void testMapFormat(String format, String expected, Map<String, String> replacements) {
        String actual = StringUtilities.format(format, replacements);
        Assertions.assertEquals(expected, actual);
    }

    public static Stream<Arguments> stringsToPrimitives() {
        return Stream.of(
                Arguments.of("1", int.class, 1),
                Arguments.of("1", long.class, 1L),
                Arguments.of("1", short.class, (short) 1),
                Arguments.of("1", byte.class, (byte) 1),
                Arguments.of("1", float.class, 1.0f),
                Arguments.of("1", double.class, 1.0d),
                Arguments.of("true", boolean.class, true),
                Arguments.of("1", char.class, '1'),
                Arguments.of("ONE", TestEnum.class, TestEnum.ONE),
                Arguments.of("TWO", TestEnum.class, TestEnum.TWO),
                Arguments.of("THREE", TestEnum.class, TestEnum.THREE)
        );
    }

    public static Stream<Arguments> invalidStringsToPrimitives() {
        return Stream.of(
                Arguments.of("one", int.class),
                Arguments.of("one", long.class),
                Arguments.of("one", short.class),
                Arguments.of("one", byte.class),
                Arguments.of("one", float.class),
                Arguments.of("one", double.class),
                Arguments.of("yes", boolean.class),
                Arguments.of("too long", char.class),
                Arguments.of("FOUR", TestEnum.class)
        );
    }

    @ParameterizedTest
    @MethodSource("stringsToPrimitives")
    <T> void testToPrimitive(String input, Class<T> type, T expected) {
        T actual = TypeUtils.toPrimitive(type, input);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testToPrimitiveDoesNotAcceptObjects() {
        Assertions.assertThrows(NotPrimitiveException.class, () -> TypeUtils.toPrimitive(Object.class, "value"));
        Assertions.assertThrows(NotPrimitiveException.class, () -> TypeUtils.toPrimitive(String.class, "value"));
        Assertions.assertThrows(NotPrimitiveException.class, () -> TypeUtils.toPrimitive(List.class, "value"));
    }

    @ParameterizedTest
    @MethodSource("invalidStringsToPrimitives")
    <T> void testToPrimitiveThrowsOnInvalidInput(String input, Class<T> type) {
        Assertions.assertThrows(TypeConversionException.class, () -> TypeUtils.toPrimitive(type, input));
    }

    @Test
    void testValidWildcardAdjustment() {
        List<?> list = Arrays.asList("one", "two", "three");
        List<String> adjusted = Assertions.assertDoesNotThrow(() -> TypeUtils.unchecked(list, List.class));
        Assertions.assertNotNull(adjusted);
        Assertions.assertSame(list, adjusted);
    }

    @Test
    void testWildcardAdjustmentDoesAdjustParent() {
        List<?> list = Arrays.asList("one", "two", "three");
        List<String> adjusted = Assertions.assertDoesNotThrow(() -> TypeUtils.unchecked(list, Collection.class));
        Assertions.assertNotNull(adjusted);
        Assertions.assertSame(list, adjusted);
    }

    @Test
    void testAnnotationCreatesEmptyAnnotation() {
        TestAnnotation annotation = TypeUtils.annotation(TestAnnotation.class);
        Assertions.assertNotNull(annotation);
    }

    @Test
    void testAnnotationCreatesAnnotationWithValues() {
        TestAnnotationWithValue annotation = TypeUtils.annotation(TestAnnotationWithValue.class, Map.of("value", "test"));
        Assertions.assertNotNull(annotation);
        Assertions.assertEquals("test", annotation.value());
    }

    @Test
    void testAnnotationValidatesValues() {
        Assertions.assertThrows(IllegalStateException.class, () -> TypeUtils.annotation(TestAnnotationWithValue.class, Map.of("value", 1)));
    }

    private @interface TestAnnotation {
    }

    private @interface TestAnnotationWithValue {
        String value();
    }

    private enum TestEnum {
        ONE,
        TWO,
        THREE
    }
}
