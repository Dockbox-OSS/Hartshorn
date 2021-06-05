/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Tuple;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

public class HartshornUtilTests {

    @Test
    void testOfEntriesAddsAllEntries() {
        Map<Integer, String> map = HartshornUtils.ofEntries(
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
        Map<Object, Object> map = HartshornUtils.ofEntries();
        Assertions.assertNotNull(map);
        Assertions.assertTrue(map.isEmpty());
    }

    @Test
    void testEmptyMapIsModifiable() {
        Map<Object, Object> map = HartshornUtils.emptyMap();
        Assertions.assertDoesNotThrow(() -> map.put(1, "two"));
    }

    @Test
    void testEntryKeepsValues() {
        Entry<Integer, String> entry = HartshornUtils.entry(1, "two");
        Assertions.assertNotNull(entry);
        Assertions.assertEquals(1, (int) entry.getKey());
        Assertions.assertEquals("two", entry.getValue());
    }

    private static Stream<Arguments> getModifiableCollections() {
        return Stream.of(
                Arguments.of(HartshornUtils.emptyList()),
                Arguments.of(HartshornUtils.emptyConcurrentList()),
                Arguments.of(HartshornUtils.asList("value", "other")),
                Arguments.of(HartshornUtils.asList(Arrays.asList("value", "other"))),
                Arguments.of(HartshornUtils.asList(Arrays.asList("value", "other"))),
                Arguments.of(HartshornUtils.emptySet()),
                Arguments.of(HartshornUtils.emptyConcurrentSet()),
                Arguments.of(HartshornUtils.asSet("value", "other")),
                Arguments.of(HartshornUtils.asSet(Arrays.asList("value", "other")))

        );
    }

    @ParameterizedTest
    @MethodSource("getModifiableCollections")
    void testCollectionCanBeModified(Collection<String> collection) {
        Assertions.assertNotNull(collection);
        Assertions.assertDoesNotThrow(() -> collection.add("another"));
    }

    private static Stream<Arguments> getUnmodifiableCollections() {
        return Stream.of(
                Arguments.of(HartshornUtils.asUnmodifiableList("value", "other")),
                Arguments.of(HartshornUtils.asUnmodifiableList(Arrays.asList("value", "other"))),
                Arguments.of(HartshornUtils.asUnmodifiableList((Collection<String>) Arrays.asList("value", "other"))),
                Arguments.of(HartshornUtils.asUnmodifiableSet("value", "other")),
                Arguments.of(HartshornUtils.asUnmodifiableSet(Arrays.asList("value", "other"))),
                Arguments.of(HartshornUtils.asUnmodifiableCollection("value", "other")),
                Arguments.of(HartshornUtils.asUnmodifiableCollection(Arrays.asList("value", "other")))

        );
    }

    @ParameterizedTest
    @MethodSource("getUnmodifiableCollections")
    void testImmutableCollectionCannotBeModified(Collection<String> collection) {
        Assertions.assertNotNull(collection);
        Assertions.assertThrows(UnsupportedOperationException.class, () -> collection.add("another"));
    }

    private static Stream<Arguments> getCapitalizeValues() {
        return Stream.of(
                Arguments.of("value", "Value"),
                Arguments.of("Value", "Value"),
                Arguments.of("VALUE", "VALUE"),
                Arguments.of(" value", " value")
        );
    }

    @ParameterizedTest
    @MethodSource("getCapitalizeValues")
    void testCapitalizeChangesOnlyFirstCharacter(String input, String expected) {
        String value = HartshornUtils.capitalize(input);
        Assertions.assertNotNull(value);
        Assertions.assertEquals(expected, value);
    }

    @Test
    void testIsEmptyStringTrueIfNull() {
        Assertions.assertTrue(HartshornUtils.isEmpty((String) null));
    }

    @Test
    void testIsEmptyStringTrueIfEmpty() {
        Assertions.assertTrue(HartshornUtils.isEmpty(""));
    }

    @Test
    void testIsEmptyStringFalseIfContent() {
        Assertions.assertFalse(HartshornUtils.isEmpty("value"));
    }

    @Test
    void testStringLengthIsZeroIfNull() {
        Assertions.assertEquals(0, HartshornUtils.length(null));
    }

    @Test
    void testStringLengthIsZeroIfEmpty() {
        Assertions.assertEquals(0, HartshornUtils.length(""));
    }

    @Test
    void testStringLengthIsCorrect() {
        Assertions.assertEquals(5, HartshornUtils.length("value"));
    }

    @Test
    void testLastIndexOfIsNegativeIfPathNull() {
        Assertions.assertEquals(-1, HartshornUtils.lastIndexOf(null, 'a'));
    }

    @Test
    void testLastIndexOfIsNegativeIfNotInPath() {
        Assertions.assertEquals(-1, HartshornUtils.lastIndexOf("other", 'a'));
    }

    @Test
    void testLastIndexOfIsCorrect() {
        Assertions.assertEquals(1, HartshornUtils.lastIndexOf("value", 'a'));
    }

    private static Stream<Arguments> getHexDigits() {
        return Stream.of(
                Arguments.of(0, '0'),
                Arguments.of(1, '1'),
                Arguments.of(2, '2'),
                Arguments.of(3, '3'),
                Arguments.of(4, '4'),
                Arguments.of(5, '5'),
                Arguments.of(6, '6'),
                Arguments.of(7, '7'),
                Arguments.of(8, '8'),
                Arguments.of(9, '9'),
                Arguments.of(0xA, 'A'),
                Arguments.of(0xB, 'B'),
                Arguments.of(0xC, 'C'),
                Arguments.of(0xD, 'D'),
                Arguments.of(0xE, 'E'),
                Arguments.of(0xF, 'F')
        );
    }

    @ParameterizedTest
    @MethodSource("getHexDigits")
    void convertHexDigitsReturnsCorrectChar(int value, char expected) {
        Assertions.assertEquals(expected, HartshornUtils.convertDigit(value));
    }

    @Test
    void testRangeMinAndMaxAreInclusive() {
        int[] range = HartshornUtils.range(0, 10);
        Assertions.assertEquals(11, range.length);
    }

    @Test
    void testRangeIsOrdered() {
        int[] range = HartshornUtils.range(0, 10);
        for (int i = 0; i <= 10; i++) {
            Assertions.assertEquals(i, range[i]);
        }
    }

    @Test
    void testShortenReturnsStringIfMaxIsLarger() {
        String value = HartshornUtils.shorten("value", 10);
        Assertions.assertEquals("value", value);
    }

    @Test
    void testShortenReturnsShortStringIfMaxIsSmaller() {
        String value = HartshornUtils.shorten("value", 3);
        Assertions.assertEquals("val", value);
    }

    @Test
    void testRepeatIsCorrect() {
        String value = HartshornUtils.repeat("a", 3);
        Assertions.assertEquals("aaa", value);
    }

    @Test
    void testRepeatIsEmptyIfAmountIsZero() {
        String value = HartshornUtils.repeat("a", 0);
        Assertions.assertEquals("", value);
    }

    @Test
    void testCountIsCorrect() {
        int count = HartshornUtils.count("aaa", 'a');
        Assertions.assertEquals(3, count);
    }

    private static Stream<Arguments> getWildcardInputs() {
        return Stream.of(
                Arguments.of("*", "^.*$"),
                Arguments.of("?", "^.$"),
                Arguments.of("(", "^\\($"),
                Arguments.of(")", "^\\)$"),
                Arguments.of("[", "^\\[$"),
                Arguments.of("]", "^\\]$"),
                Arguments.of("$", "^\\$$"),
                Arguments.of("^", "^\\^$"),
                Arguments.of(".", "^\\.$"),
                Arguments.of("{", "^\\{$"),
                Arguments.of("}", "^\\}$"),
                Arguments.of("|", "^\\|$"),
                Arguments.of("\\", "^\\\\$"),
                Arguments.of("[a-z]", "^\\[a-z\\]$"),
                Arguments.of("[A-Z]", "^\\[A-Z\\]$"),
                Arguments.of("[0-9]", "^\\[0-9\\]$"),
                Arguments.of("[a-zA-Z0-9]", "^\\[a-zA-Z0-9\\]$"),
                Arguments.of("[!a-z]", "^\\[!a-z\\]$"),
                Arguments.of("#", "^#$"),
                Arguments.of("*value*", "^.*value.*$")
        );
    }

    @ParameterizedTest
    @MethodSource("getWildcardInputs")
    void testWildcardToRegexOutput(String wildcard, String regex) {
        String outputRegex = HartshornUtils.wildcardToRegexString(wildcard);
        Assertions.assertNotNull(outputRegex);
        Assertions.assertEquals(regex, outputRegex);
    }

    private static Stream<Arguments> getLevenshteinDistances() {
        return Stream.of(
                Arguments.of("kitten", "kitten", 0),
                Arguments.of("kitten", "smitten", 2),
                Arguments.of("kitten", "mitten", 1),
                Arguments.of("kitten", "kitty", 2),
                Arguments.of("kitten", "fitting", 3),
                Arguments.of("kitten", "written", 2)
        );
    }

    @ParameterizedTest
    @MethodSource("getLevenshteinDistances")
    void testLevenshteinDistance(String source, String target, int distance) {
        int actualDistance = HartshornUtils.levenshteinDistance(source, target);
        Assertions.assertEquals(distance, actualDistance);
    }

    @ParameterizedTest
    @MethodSource("getLevenshteinDistances")
    void testDamerauLevenshteinDistance(String source, String target, int distance) {
        int actualDistance = HartshornUtils.damerauLevenshteinDistance(source, target);
        Assertions.assertEquals(distance, actualDistance);
    }

    @Test
    void testMinimumLongIsCorrect() {
        long minimum = HartshornUtils.minimum(1L, 10L, 100L);
        Assertions.assertEquals(1L, minimum);
    }

    @Test
    void testMinimumDoubleIsCorrect() {
        double minimum = HartshornUtils.minimum(1.5D, 1.0D, 1.3D);
        Assertions.assertEquals(1.0D, minimum);
    }

    @Test
    void testMaximumLongIsCorrect() {
        long minimum = HartshornUtils.maximum(1L, 10L, 100L);
        Assertions.assertEquals(100L, minimum);
    }

    @Test
    void testMaximumDoubleIsCorrect() {
        double minimum = HartshornUtils.maximum(1.5D, 1.0D, 1.3D);
        Assertions.assertEquals(1.5D, minimum);
    }

    @ParameterizedTest
    @MethodSource("getCapitalizeValues")
    void testHashIgnoreCase(String first, String second) {
        int firstHash = HartshornUtils.hashCodeIgnoreCase(first);
        int secondHash = HartshornUtils.hashCodeIgnoreCase(second);
        Assertions.assertEquals(firstHash, secondHash);
    }

    @Test
    void testArraySizeIsZeroIfArrayNull() {
        Assertions.assertEquals(0, HartshornUtils.size((Object[]) null));
    }

    @Test
    void testArraySizeIsZeroIfArrayEmpty() {
        Assertions.assertEquals(0, HartshornUtils.size());
    }

    @Test
    void testArraySizeIsCorrect() {
        Assertions.assertEquals(3, HartshornUtils.size('A', "B", 3));
    }

    @Test
    void testArrayRemoveIsCorrect() {
        Integer[] array = HartshornUtils.removeItem(new Integer[]{ 1, 2, 3, 4, 5 }, 2);
        Assertions.assertEquals(4, array.length);
        Assertions.assertNotEquals(3, array[2]);
    }

    @Test
    void testArraySubsetIsCorrect() {
        Integer[] array = HartshornUtils.getArraySubset(new Integer[]{ 1, 2, 3, 4, 5 }, 1, 3);
        Assertions.assertEquals(3, array.length);
        Assertions.assertNotEquals(1, array[0]);
        Assertions.assertNotEquals(5, array[2]);
    }

    private static Stream<Arguments> getRoundingValues() {
        return Stream.of(
                Arguments.of(20.123456, 20, 0),
                Arguments.of(20.123456, 20.1, 1),
                Arguments.of(20.123456, 20.12, 2),
                Arguments.of(20.123456, 20.123, 3),
                Arguments.of(20.123456, 20.1235, 4), // Rounds up to ..5
                Arguments.of(20.123456, 20.12346, 5), // Rounds up to ..6
                Arguments.of(20.123456, 20.123456, 6),
                Arguments.of(20.123456, 20.1234560, 7)
        );
    }

    @ParameterizedTest
    @MethodSource("getRoundingValues")
    void testRoundIsCorrect(double original, double expected, int decimals) {
        double rounded = HartshornUtils.round(original, decimals);
        Assertions.assertEquals(expected, rounded);
    }

    @Test
    void testUnwrapIsFalseIfAbsent() {
        Assertions.assertFalse(HartshornUtils.unwrap(Optional.empty()));
    }

    @Test
    void testUnwrapIsFalseIfFalse() {
        Assertions.assertFalse(HartshornUtils.unwrap(Optional.of(false)));
    }

    @Test
    void testUnwrapIsTrueIfTrue() {
        Assertions.assertTrue(HartshornUtils.unwrap(Optional.of(true)));
    }

    private static Stream<Arguments> getRegionValues() {
        return Stream.of(
                Arguments.of(Vector3N.of(0,0,0), Vector3N.of(2,2,2), Vector3N.of(1,1,1), true),
                Arguments.of(Vector3N.of(0,0,0), Vector3N.of(1,1,1), Vector3N.of(2,2,2), false),
                Arguments.of(Vector3N.of(0,0,0), Vector3N.of(2,2,2), Vector3N.of(1,1,3), false),
                Arguments.of(Vector3N.of(0,0,0), Vector3N.of(2,2,2), Vector3N.of(1,3,1), false),
                Arguments.of(Vector3N.of(0,0,0), Vector3N.of(2,2,2), Vector3N.of(3,1,1), false),
                Arguments.of(Vector3N.of(0,0,0), Vector3N.of(2,2,2), Vector3N.of(3,3,3), false)
        );
    }

    @ParameterizedTest
    @MethodSource("getRegionValues")
    void testIsInCuboidRegionVector(Vector3N min, Vector3N max, Vector3N vec, boolean inside) {
        Assertions.assertEquals(inside, HartshornUtils.isInCuboidRegion(min, max, vec));
    }

    @ParameterizedTest
    @MethodSource("getRegionValues")
    void testIsInCuboidRegionInt(Vector3N min, Vector3N max, Vector3N vec, boolean inside) {
        Assertions.assertEquals(inside, HartshornUtils.isInCuboidRegion(
                min.getXi(), max.getXi(),
                min.getYi(), max.getYi(),
                min.getZi(), max.getZi(),
                vec.getXi(), vec.getYi(), vec.getZi()));
    }

    private static Stream<Arguments> getMinimumValues() {
        return Stream.of(
                Arguments.of(Vector3N.of(0,0,0), Vector3N.of(10,10,10), Vector3N.of(0,0,0)),
                Arguments.of(Vector3N.of(0,10,10), Vector3N.of(10,0,0), Vector3N.of(0,0,0)),
                Arguments.of(Vector3N.of(10,10,10), Vector3N.of(10,10,10), Vector3N.of(10,10,10))
        );
    }

    @ParameterizedTest
    @MethodSource("getMinimumValues")
    void testMinimumPoint(Vector3N pos1, Vector3N pos2, Vector3N min) {
        Vector3N minimumPoint = HartshornUtils.getMinimumPoint(pos1, pos2);
        Assertions.assertEquals(min, minimumPoint);
    }

    private static Stream<Arguments> getMaximumValues() {
        return Stream.of(
                Arguments.of(Vector3N.of(0,0,0), Vector3N.of(10,10,10), Vector3N.of(10,10,10)),
                Arguments.of(Vector3N.of(0,10,10), Vector3N.of(10,0,0), Vector3N.of(10,10,10)),
                Arguments.of(Vector3N.of(10,10,10), Vector3N.of(10,10,10), Vector3N.of(10,10,10))
        );
    }

    @ParameterizedTest
    @MethodSource("getMaximumValues")
    void testMaximumPoint(Vector3N pos1, Vector3N pos2, Vector3N max) {
        Vector3N maximumPoint = HartshornUtils.getMaximumPoint(pos1, pos2);
        Assertions.assertEquals(max, maximumPoint);
    }

    @Test
    void testToLocalDateTimeIsCorrect() {
        Instant instant = Instant.ofEpochSecond(((20 * 365) + 5) * 24 * 60 * 60);// 20 years after 1970, correcting for 5x February 29
        LocalDateTime expectedDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        LocalDateTime dateTime = HartshornUtils.toLocalDateTime(instant);
        Assertions.assertNotNull(dateTime);
        Assertions.assertEquals(expectedDate, dateTime);
    }

    @Test
    void testArrayMerge() {
        Object[] arr1 = {1,2,3};
        Object[] arr2 = {4,5,6};
        Object[] merged = HartshornUtils.merge(arr1, arr2);
        for (int i = 0; i < 6; i++) {
            Assertions.assertEquals(i+1, (int) merged[i]);
        }
    }

    @Test
    void testCollectionMerge() {
        Collection<Integer> col1 = Arrays.asList(1,2,3);
        Collection<Integer> col2 = Arrays.asList(4,5,6);
        Collection<?> merged = HartshornUtils.merge(col1, col2);

        Assertions.assertEquals(6, merged.size());
        Assertions.assertTrue(merged.containsAll(Arrays.asList(1,2,3,4,5,6)));
    }

    @Test
    void testArrayShallowCopy() {
        Object[] arr1 = {1,2,3};
        Object[] arr2 = HartshornUtils.shallowCopy(arr1);
        Assertions.assertNotSame(arr1, arr2);
        Assertions.assertArrayEquals(arr1, arr2);
    }

    private static Stream<Arguments> getEmptyValues() {
        return Stream.of(
                Arguments.of(null, true),
                Arguments.of("", true),
                Arguments.of("value", false),
                Arguments.of(HartshornUtils.emptyList(), true),
                Arguments.of(HartshornUtils.emptySet(), true),
                Arguments.of(HartshornUtils.emptyConcurrentList(), true),
                Arguments.of(HartshornUtils.emptyConcurrentSet(), true),
                Arguments.of(HartshornUtils.asList("value"), false),
                Arguments.of(HartshornUtils.emptyMap(), true),
                Arguments.of(HartshornUtils.emptyConcurrentMap(), true),
                Arguments.of(HartshornUtils.ofEntries(Tuple.of(1, "two")), false),
                Arguments.of(new EmptyType(), true),
                Arguments.of(new RejectingType(), false)
        );
    }

    @ParameterizedTest
    @MethodSource("getEmptyValues")
    void testIsEmpty(Object obj, boolean empty) {
        System.out.println("Got: " + obj);
        Assertions.assertEquals(empty, HartshornUtils.isEmpty(obj));
    }

    private static Stream<Arguments> getStringEqualityValues() {
        return Stream.of(
                Arguments.of("value", "value", true),
                Arguments.of("value", "VALUE", false)
        );
    }

    @ParameterizedTest
    @MethodSource("getStringEqualityValues")
    void testEqualsStrings(String s1, String s2, boolean equal) {
        Assertions.assertEquals(equal, HartshornUtils.equals(s1, s2));
    }

    private static Stream<Arguments> getStringEqualityIgnoreCaseValues() {
        return Stream.of(
                Arguments.of("value", "value", true),
                Arguments.of("value", "VALUE", true),
                Arguments.of("value", "other", false)
        );
    }

    @ParameterizedTest
    @MethodSource("getStringEqualityIgnoreCaseValues")
    void testEqualsIgnoreCaseStrings(String s1, String s2, boolean equal) {
        Assertions.assertEquals(equal, HartshornUtils.equalsIgnoreCase(s1, s2));
    }

    private static Stream<Arguments> getStringEqualityTrimValues() {
        return Stream.of(
                Arguments.of("value", "value", true),
                Arguments.of("value", " value ", true),
                Arguments.of("value", " value", true),
                Arguments.of("value", "value ", true),
                Arguments.of("value", "other", false)
        );
    }

    @ParameterizedTest
    @MethodSource("getStringEqualityTrimValues")
    void testEqualsTrimStrings(String s1, String s2, boolean equal) {
        Assertions.assertEquals(equal, HartshornUtils.equalsWithTrim(s1, s2));
    }

    private static Stream<Arguments> getStringEqualityTrimIgnoreCaseValues() {
        return Stream.of(
                Arguments.of("value", "value", true),
                Arguments.of("value", " value ", true),
                Arguments.of("value", " value", true),
                Arguments.of("value", "value ", true),
                Arguments.of("value", "VALUE ", true),
                Arguments.of("value", " VALUE", true),
                Arguments.of("value", " VALUE ", true),
                Arguments.of("value", "other", false)
        );
    }

    @ParameterizedTest
    @MethodSource("getStringEqualityTrimIgnoreCaseValues")
    void testEqualsTrimIgnoreCaseStrings(String s1, String s2, boolean equal) {
        Assertions.assertEquals(equal, HartshornUtils.equalsIgnoreCaseWithTrim(s1, s2));
    }

    private static Stream<Arguments> getNonEqualValues() {
        return Stream.of(
                Arguments.of("value", "value", false),
                Arguments.of("value", "VALUE", true),
                Arguments.of("value", 1, true),
                Arguments.of(1.2D, 1, true)
        );
    }

    @ParameterizedTest
    @MethodSource("getNonEqualValues")
    void testNotEqual(Object o1, Object o2, boolean expected) {
        Assertions.assertEquals(expected, HartshornUtils.notEqual(o1, o2));
    }

    private static Stream<Arguments> getEqualValues() {
        return Stream.of(
                Arguments.of("value", "value", true),
                Arguments.of(null, null, false),
                Arguments.of(null, "value", false),
                Arguments.of("value", "VALUE", false),
                Arguments.of("value", 1, false),
                Arguments.of(1.2D, 1, false)
        );
    }

    @ParameterizedTest
    @MethodSource("getEqualValues")
    void testEqual(Object o1, Object o2, boolean expected) {
        Assertions.assertEquals(expected, HartshornUtils.equal(o1, o2));
    }

    private static Stream<Arguments> getSameValues() {
        return Stream.of(
                Arguments.of("value"),
                Arguments.of(new ArrayList<>()),
                Arguments.of(-1),
                Arguments.of((Object) null)
        );
    }

    @ParameterizedTest
    @MethodSource("getSameValues")
    void testSame(Object obj) {
        Assertions.assertTrue(HartshornUtils.same(obj, obj));
    }

    private static Stream<Arguments> getContentValues() {
        return Stream.of(
                Arguments.of("value", true),
                Arguments.of("value ", true),
                Arguments.of("", false),
                Arguments.of("  ", false),
                Arguments.of(null, false)
        );
    }

    @ParameterizedTest
    @MethodSource("getContentValues")
    void testHasContent(String s, boolean content) {
        Assertions.assertEquals(content, HartshornUtils.hasContent(s));
    }

    @Test
    void testDoesNotThrow() {
        Assertions.assertTrue(HartshornUtils.doesNotThrow(() -> {
            int i = 1+1;
        }));
        Assertions.assertFalse(HartshornUtils.doesNotThrow(() -> {
            throw new Exception("error");
        }));
    }

    @Test
    void testDoesNotThrowSpecific() {
        Assertions.assertTrue(HartshornUtils.doesNotThrow(() -> {
            int i = 1+1;
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
            int i = 1+1;
        }, Exception.class));
        Assertions.assertTrue(HartshornUtils.throwsException(() -> {
            throw new UnsupportedOperationException("error");
        }, UnsupportedOperationException.class));
        Assertions.assertFalse(HartshornUtils.throwsException(() -> {
            throw new IllegalArgumentException("error");
        }, UnsupportedOperationException.class));
    }

    private static final int minute = 60;
    private static final int hour = 60*minute;
    private static final int day = 24*hour;
    private static final int week = 7*day;

    private static Stream<Arguments> getDurations() {
        return Stream.of(
                Arguments.of("1s", 1),
                Arguments.of("1m", minute),
                Arguments.of("1h", hour),
                Arguments.of("1d", day),
                Arguments.of("1w", week),
                Arguments.of("1w1d1h1m1s", week + day + hour + minute + 1),
                Arguments.of("2w3d", (2*week) + (3*day)),
                Arguments.of("2w3d5h", (2*week) + (3*day) + (5*hour)),
                Arguments.of("17h21m13s", (17*hour) + (21*minute) + 13)
        );
    }

    @ParameterizedTest
    @MethodSource("getDurations")
    void testDurationOf(String in, long expected) {
        Exceptional<Duration> duration = HartshornUtils.durationOf(in);
        Assertions.assertTrue(duration.present());
        Assertions.assertEquals(expected, duration.get().getSeconds());
    }

    @Test
    void testToTableString() {
        List<List<String>> rows = HartshornUtils.emptyList();
        rows.add(HartshornUtils.asList("h1", "h2", "h3"));
        rows.add(HartshornUtils.asList("v1", "v2", "v3"));
        String table = HartshornUtils.asTable(rows);

        Assertions.assertNotNull(table);
        Assertions.assertEquals("h1  h2  h3  \n" +
                        "v1  v2  v3  \n",
                table);
    }
}
