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

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.domain.tuple.Tuple;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class HartshornUtilTests {

    private static final int minute = 60;
    private static final int hour = 60 * minute;
    private static final int day = 24 * hour;
    private static final int week = 7 * day;

    private static Stream<Arguments> modifiableCollections() {
        return Stream.of(
                Arguments.of(HartshornUtils.asList("value", "other")),
                Arguments.of(HartshornUtils.emptyConcurrentSet()),
                Arguments.of(HartshornUtils.asSet(Arrays.asList("value", "other")))

        );
    }

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
                Arguments.of(HartshornUtils.asList("a", "b"), HartshornUtils.asList("a"), HartshornUtils.asList("b")),
                Arguments.of(HartshornUtils.asList("a"), HartshornUtils.asList("a", "b"), HartshornUtils.asList("b"))
        );
    }

    @Test
    void testOfEntriesAddsAllEntries() {
        final Map<Integer, String> map = HartshornUtils.ofEntries(
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
        final Map<Object, Object> map = HartshornUtils.ofEntries();
        Assertions.assertNotNull(map);
        Assertions.assertTrue(map.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("modifiableCollections")
    void testCollectionCanBeModified(final Collection<String> collection) {
        Assertions.assertNotNull(collection);
        Assertions.assertDoesNotThrow(() -> collection.add("another"));
    }

    @ParameterizedTest
    @MethodSource("capitalizeValues")
    void testCapitalizeChangesOnlyFirstCharacter(final String input, final String expected) {
        final String value = HartshornUtils.capitalize(input);
        Assertions.assertNotNull(value);
        Assertions.assertEquals(expected, value);
    }

    @Test
    void testIsEmptyStringTrueIfNull() {
        Assertions.assertTrue(HartshornUtils.empty((String) null));
    }

    @Test
    void testIsEmptyStringTrueIfEmpty() {
        Assertions.assertTrue(HartshornUtils.empty(""));
    }

    @Test
    void testIsEmptyStringFalseIfContent() {
        Assertions.assertFalse(HartshornUtils.empty("value"));
    }

    @Test
    void testArrayMerge() {
        final Object[] arr1 = { 1, 2, 3 };
        final Object[] arr2 = { 4, 5, 6 };
        final Object[] merged = HartshornUtils.merge(arr1, arr2);
        for (int i = 0; i < 6; i++) {
            Assertions.assertEquals(i + 1, (int) merged[i]);
        }
    }

    @Test
    void testCollectionMerge() {
        final Collection<Integer> col1 = Arrays.asList(1, 2, 3);
        final Collection<Integer> col2 = Arrays.asList(4, 5, 6);
        final Collection<Integer> merged = HartshornUtils.merge(col1, col2);

        Assertions.assertEquals(6, merged.size());
        Assertions.assertTrue(merged.containsAll(Arrays.asList(1, 2, 3, 4, 5, 6)));
    }

    @Test
    void testDoesNotThrow() {
        Assertions.assertTrue(HartshornUtils.doesNotThrow(() -> {
            final int i = 1 + 1;
        }));
        Assertions.assertFalse(HartshornUtils.doesNotThrow(() -> {
            throw new ApplicationException("error");
        }));
    }

    @Test
    void testDoesNotThrowSpecific() {
        Assertions.assertTrue(HartshornUtils.doesNotThrow(() -> {
            final int i = 1 + 1;
        }, Exception.class));
        Assertions.assertFalse(HartshornUtils.doesNotThrow(() -> {
            throw new UnsupportedOperationException("error");
        }, UnsupportedOperationException.class));
        Assertions.assertTrue(HartshornUtils.doesNotThrow(() -> {
            throw new IllegalArgumentException("error");
        }, UnsupportedOperationException.class));
    }

    @Test
    void testThrowsSpecific() {
        Assertions.assertFalse(HartshornUtils.throwsException(() -> {
            final int i = 1 + 1;
        }, Exception.class));
        Assertions.assertTrue(HartshornUtils.throwsException(() -> {
            throw new UnsupportedOperationException("error");
        }, UnsupportedOperationException.class));
        Assertions.assertFalse(HartshornUtils.throwsException(() -> {
            throw new IllegalArgumentException("error");
        }, UnsupportedOperationException.class));
    }

    @ParameterizedTest
    @MethodSource("durations")
    void testDurationOf(final String in, final long expected) {
        final Exceptional<Duration> duration = HartshornUtils.durationOf(in);
        Assertions.assertTrue(duration.present());
        Assertions.assertEquals(expected, duration.get().getSeconds());
    }

    @Test
    void testToTableString() {
        final List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("h1", "h2", "h3"));
        rows.add(List.of("v1", "v2", "v3"));
        final String table = HartshornUtils.asTable(rows);

        Assertions.assertNotNull(table);
        Assertions.assertEquals("""
                        h1  h2  h3 \s
                        v1  v2  v3 \s
                        """,
                table);
    }

    @ParameterizedTest
    @MethodSource("differences")
    void testDifferenceInCollections(final Collection<String> a, final Collection<String> b, final Collection<String> expected) {
        final Set<String> difference = HartshornUtils.difference(a, b);
        Assertions.assertEquals(difference.size(), expected.size());
        Assertions.assertTrue(difference.containsAll(expected));
        Assertions.assertTrue(expected.containsAll(difference));
    }
}
