/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.util.Exceptional;
import org.dockbox.hartshorn.util.Tuple;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.StringUtilities;
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

    private static Stream<Arguments> equalValues() {
        return Stream.of(
                Arguments.of("value", "value", true),
                Arguments.of(null, null, false),
                Arguments.of(null, "value", false),
                Arguments.of("value", "VALUE", false),
                Arguments.of("value", 1, false),
                Arguments.of(1.2D, 1, false)
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

    @Test
    void testOfEntriesAddsAllEntries() {
        final Map<Integer, String> map = CollectionUtilities.ofEntries(
                Tuple.of(1, "two"),
                Tuple.of(2, "three")
        );
        Assertions.assertTrue(map.containsKey(1));
        Assertions.assertTrue(map.containsKey(2));
        Assertions.assertTrue(map.containsValue("two"));
        Assertions.assertTrue(map.containsValue("three"));
    }

    @Test
    void testOfEntriesIsEmptyWithNoEntries() {
        final Map<Object, Object> map = CollectionUtilities.ofEntries();
        Assertions.assertNotNull(map);
        Assertions.assertTrue(map.isEmpty());
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
        Assertions.assertTrue(StringUtilities.empty((String) null));
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
        final Exceptional<Duration> duration = StringUtilities.durationOf(in);
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
}
