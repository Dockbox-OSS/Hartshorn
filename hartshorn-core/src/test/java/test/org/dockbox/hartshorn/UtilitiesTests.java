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

package test.org.dockbox.hartshorn;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.Tuple;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
                Arguments.of("Hello %s!", "Hello %s!", new Object[] {"world"}),
                Arguments.of("Hello {0}!", "Hello world!", new Object[] {"world"}),
                Arguments.of("{0} {1}!", "Hello world!", new Object[] {"Hello", "world"}),
                Arguments.of("{0} {0}!", "Hello Hello!", new Object[] {"Hello", "world"})
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
    void testCapitalizeChangesOnlyFirstCharacter(final String input, final String expected) {
        final String value = StringUtilities.capitalize(input);
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
    void testCollectionMerge() {
        final Collection<Integer> col1 = Arrays.asList(1, 2, 3);
        final Collection<Integer> col2 = Arrays.asList(4, 5, 6);
        final Collection<Integer> merged = CollectionUtilities.merge(col1, col2);

        Assertions.assertEquals(6, merged.size());
        Assertions.assertTrue(merged.containsAll(Arrays.asList(1, 2, 3, 4, 5, 6)));
    }

    @ParameterizedTest
    @MethodSource("durations")
    void testDurationOf(final String in, final long expected) {
        final Option<Duration> duration = StringUtilities.durationOf(in);
        Assertions.assertTrue(duration.present());
        Assertions.assertEquals(expected, duration.get().getSeconds());
    }

    @ParameterizedTest
    @MethodSource("differences")
    void testDifferenceInCollections(final Collection<String> a, final Collection<String> b, final Collection<String> expected) {
        final Set<String> difference = CollectionUtilities.difference(a, b);
        Assertions.assertEquals(difference.size(), expected.size());
        Assertions.assertTrue(difference.containsAll(expected));
        Assertions.assertTrue(expected.containsAll(difference));
    }

    @Test
    void testSplitCapitals() {
        final String input = "ThisIsAString";
        final String[] expected = new String[] { "This", "Is", "A", "String" };
        final String[] actual = StringUtilities.splitCapitals(input);
        Assertions.assertArrayEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("stringFormats")
    void testFormat(final String format, final String expected, final Object... args) {
        final String actual = StringUtilities.format(format, args);
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("stringMapFormats")
    void testMapFormat(final String format, final String expected, final Map<String, String> replacements) {
        final String actual = StringUtilities.format(format, replacements);
        Assertions.assertEquals(expected, actual);
    }
}
