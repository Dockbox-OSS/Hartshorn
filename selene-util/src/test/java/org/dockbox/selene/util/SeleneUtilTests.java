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

package org.dockbox.selene.util;

import org.dockbox.selene.api.domain.tuple.Tuple;
import org.dockbox.selene.api.domain.tuple.Vector3N;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

public class SeleneUtilTests {

    @Test
    void testOfEntriesAddsAllEntries() {
        Map<Integer, String> map = SeleneUtils.ofEntries(
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
        Map<Object, Object> map = SeleneUtils.ofEntries();
        Assertions.assertNotNull(map);
        Assertions.assertTrue(map.isEmpty());
    }

    @Test
    void testEmptyMapIsModifiable() {
        Map<Object, Object> map = SeleneUtils.emptyMap();
        Assertions.assertDoesNotThrow(() -> map.put(1, "two"));
    }

    @Test
    void testEntryKeepsValues() {
        Entry<Integer, String> entry = SeleneUtils.entry(1, "two");
        Assertions.assertNotNull(entry);
        Assertions.assertEquals(1, (int) entry.getKey());
        Assertions.assertEquals("two", entry.getValue());
    }

    private static Stream<Arguments> getModifiableCollections() {
        return Stream.of(
                Arguments.of(SeleneUtils.emptyList()),
                Arguments.of(SeleneUtils.emptyConcurrentList()),
                Arguments.of(SeleneUtils.asList("value", "other")),
                Arguments.of(SeleneUtils.asList(Arrays.asList("value", "other"))),
                Arguments.of(SeleneUtils.asList(Arrays.asList("value", "other"))),
                Arguments.of(SeleneUtils.emptySet()),
                Arguments.of(SeleneUtils.emptyConcurrentSet()),
                Arguments.of(SeleneUtils.asSet("value", "other")),
                Arguments.of(SeleneUtils.asSet(Arrays.asList("value", "other")))

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
                Arguments.of(SeleneUtils.asUnmodifiableList("value", "other")),
                Arguments.of(SeleneUtils.asUnmodifiableList(Arrays.asList("value", "other"))),
                Arguments.of(SeleneUtils.asUnmodifiableList((Collection<String>) Arrays.asList("value", "other"))),
                Arguments.of(SeleneUtils.asUnmodifiableSet("value", "other")),
                Arguments.of(SeleneUtils.asUnmodifiableSet(Arrays.asList("value", "other"))),
                Arguments.of(SeleneUtils.asUnmodifiableCollection("value", "other")),
                Arguments.of(SeleneUtils.asUnmodifiableCollection(Arrays.asList("value", "other")))

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
        String value = SeleneUtils.capitalize(input);
        Assertions.assertNotNull(value);
        Assertions.assertEquals(expected, value);
    }

    @Test
    void testIsEmptyStringTrueIfNull() {
        Assertions.assertTrue(SeleneUtils.isEmpty((String) null));
    }

    @Test
    void testIsEmptyStringTrueIfEmpty() {
        Assertions.assertTrue(SeleneUtils.isEmpty(""));
    }

    @Test
    void testIsEmptyStringFalseIfContent() {
        Assertions.assertFalse(SeleneUtils.isEmpty("value"));
    }

    @Test
    void testStringLengthIsZeroIfNull() {
        Assertions.assertEquals(0, SeleneUtils.length(null));
    }

    @Test
    void testStringLengthIsZeroIfEmpty() {
        Assertions.assertEquals(0, SeleneUtils.length(""));
    }

    @Test
    void testStringLengthIsCorrect() {
        Assertions.assertEquals(5, SeleneUtils.length("value"));
    }

    @Test
    void testLastIndexOfIsNegativeIfPathNull() {
        Assertions.assertEquals(-1, SeleneUtils.lastIndexOf(null, 'a'));
    }

    @Test
    void testLastIndexOfIsNegativeIfNotInPath() {
        Assertions.assertEquals(-1, SeleneUtils.lastIndexOf("other", 'a'));
    }

    @Test
    void testLastIndexOfIsCorrect() {
        Assertions.assertEquals(1, SeleneUtils.lastIndexOf("value", 'a'));
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
        Assertions.assertEquals(expected, SeleneUtils.convertDigit(value));
    }

    @Test
    void testRangeMinAndMaxAreInclusive() {
        int[] range = SeleneUtils.range(0, 10);
        Assertions.assertEquals(11, range.length);
    }

    @Test
    void testRangeIsOrdered() {
        int[] range = SeleneUtils.range(0, 10);
        for (int i = 0; i <= 10; i++) {
            Assertions.assertEquals(i, range[i]);
        }
    }

    @Test
    void testShortenReturnsStringIfMaxIsLarger() {
        String value = SeleneUtils.shorten("value", 10);
        Assertions.assertEquals("value", value);
    }

    @Test
    void testShortenReturnsShortStringIfMaxIsSmaller() {
        String value = SeleneUtils.shorten("value", 3);
        Assertions.assertEquals("val", value);
    }

    @Test
    void testRepeatIsCorrect() {
        String value = SeleneUtils.repeat("a", 3);
        Assertions.assertEquals("aaa", value);
    }

    @Test
    void testRepeatIsEmptyIfAmountIsZero() {
        String value = SeleneUtils.repeat("a", 0);
        Assertions.assertEquals("", value);
    }

    @Test
    void testCountIsCorrect() {
        int count = SeleneUtils.count("aaa", 'a');
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
        String outputRegex = SeleneUtils.wildcardToRegexString(wildcard);
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
        int actualDistance = SeleneUtils.levenshteinDistance(source, target);
        Assertions.assertEquals(distance, actualDistance);
    }

    @ParameterizedTest
    @MethodSource("getLevenshteinDistances")
    void testDamerauLevenshteinDistance(String source, String target, int distance) {
        int actualDistance = SeleneUtils.damerauLevenshteinDistance(source, target);
        Assertions.assertEquals(distance, actualDistance);
    }

    @Test
    void testMinimumLongIsCorrect() {
        long minimum = SeleneUtils.minimum(1L, 10L, 100L);
        Assertions.assertEquals(1L, minimum);
    }

    @Test
    void testMinimumDoubleIsCorrect() {
        double minimum = SeleneUtils.minimum(1.5D, 1.0D, 1.3D);
        Assertions.assertEquals(1.0D, minimum);
    }

    @Test
    void testMaximumLongIsCorrect() {
        long minimum = SeleneUtils.maximum(1L, 10L, 100L);
        Assertions.assertEquals(100L, minimum);
    }

    @Test
    void testMaximumDoubleIsCorrect() {
        double minimum = SeleneUtils.maximum(1.5D, 1.0D, 1.3D);
        Assertions.assertEquals(1.5D, minimum);
    }

    @ParameterizedTest
    @MethodSource("getCapitalizeValues")
    void testHashIgnoreCase(String first, String second) {
        int firstHash = SeleneUtils.hashCodeIgnoreCase(first);
        int secondHash = SeleneUtils.hashCodeIgnoreCase(second);
        Assertions.assertEquals(firstHash, secondHash);
    }

    @Test
    void testArraySizeIsZeroIfArrayNull() {
        Assertions.assertEquals(0, SeleneUtils.size((Object[]) null));
    }

    @Test
    void testArraySizeIsZeroIfArrayEmpty() {
        Assertions.assertEquals(0, SeleneUtils.size());
    }

    @Test
    void testArraySizeIsCorrect() {
        Assertions.assertEquals(3, SeleneUtils.size('A', "B", 3));
    }

    @Test
    void testArrayRemoveIsCorrect() {
        Integer[] array = SeleneUtils.removeItem(new Integer[]{ 1, 2, 3, 4, 5 }, 2);
        Assertions.assertEquals(4, array.length);
        Assertions.assertNotEquals(3, array[2]);
    }

    @Test
    void testArraySubsetIsCorrect() {
        Integer[] array = SeleneUtils.getArraySubset(new Integer[]{ 1, 2, 3, 4, 5 }, 1, 3);
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
        double rounded = SeleneUtils.round(original, decimals);
        Assertions.assertEquals(expected, rounded);
    }

    @Test
    void testUnwrapIsFalseIfAbsent() {
        Assertions.assertFalse(SeleneUtils.unwrap(Optional.empty()));
    }

    @Test
    void testUnwrapIsFalseIfFalse() {
        Assertions.assertFalse(SeleneUtils.unwrap(Optional.of(false)));
    }

    @Test
    void testUnwrapIsTrueIfTrue() {
        Assertions.assertTrue(SeleneUtils.unwrap(Optional.of(true)));
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
    void testIsInCuboidRegion(Vector3N min, Vector3N max, Vector3N vec, boolean inside) {
        Assertions.assertEquals(inside, SeleneUtils.isInCuboidRegion(min, max, vec));
    }

    // CONTINUE FROM ISINCUBOIDREGION(INT...)
}
