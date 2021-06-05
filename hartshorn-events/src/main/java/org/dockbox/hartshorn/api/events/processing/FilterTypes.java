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

package org.dockbox.hartshorn.api.events.processing;

import org.dockbox.hartshorn.api.events.annotations.filter.Filter;
import org.dockbox.hartshorn.util.Reflect;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

/**
 * The default filter types used in {@link Filter}. These typically compare the given value of a
 * event property to a expected value.
 */
public enum FilterTypes implements FilterType {
    /**
     * Checks whether or not a expected {@link String} is contained in the actual value. If either
     * value is {@code null} or the types are not both {@link String} types, {@code false} is
     * returned.
     */
    CONTAINS((expected, actual) -> {
        if (eitherNull(expected, actual)) return false;

        if (checkTypes(expected, actual, String.class)) {
            return ((String) actual).contains((CharSequence) expected);
        }

        return false;
    }),

    /**
     * Checks whether or not a expected object equals the actual value. If both values are {@code
     * null}, are equal, or are the same object, {@code true} is returned. If either value is {@code
     * null} or they do not equal {@code false} is returned.
     */
    EQUALS((expected, actual) -> {
        if (bothNull(expected, actual)) return true;
        else if (eitherNull(expected, actual)) return false;

        if (expected == actual) return true;
        else return expected.equals(actual);
    }),

    /**
     * Checks whether both values are numeral types, then compares if the expected value is greater
     * than the actual value.
     */
    GREATER_THAN((expected, actual) -> {
        return numberCheck(expected, actual, (e, a) -> e > a);
    }),

    /**
     * Checks whether both values are numeral types, then compares if the expected value is less than
     * the actual value.
     */
    LESS_THAN((expected, actual) -> {
        return numberCheck(expected, actual, (e, a) -> e < a);
    }),

    /**
     * Checks whether both values are numeral types, then compares if the expected value is less than-
     * or equal to the actual value.
     */
    LESS_OR_EQUAL((expected, actual) -> {
        return numberCheck(expected, actual, (e, a) -> e <= a);
    }),

    /**
     * Checks whether both values are numeral types, then compares if the expected value is greater
     * than- or equal to the actual value.
     */
    GREATER_OR_EQUAL((expected, actual) -> {
        return numberCheck(expected, actual, (e, a) -> e >= a);
    }),

    /**
     * Checks whether or not a expected object does not equal the actual value. If both values are
     * {@code null}, are equal, or are the same object, {@code false} is returned. If either value is
     * {@code null} or they do not equal {@code true} is returned. (Inverse of {@link #EQUALS}.
     */
    NOT_EQUAL((expected, actual) -> !EQUALS.test(expected, actual)),

    /**
     * Checks that a expected {@link String} is not contained in the actual value. (Inverse of {@link
     * #CONTAINS}.
     */
    NOT_CONTAINS((expected, actual) -> !CONTAINS.test(expected, actual)),
    ;

    private final BiFunction<Object, Object, Boolean> defaultFilter;

    FilterTypes(BiFunction<Object, Object, Boolean> defaultFilter) {
        this.defaultFilter = defaultFilter;
    }

    private static boolean numberCheck(
            Object expected, Object actual, BiFunction<Float, Float, Boolean> function) {
        if (eitherNull(expected, actual)) return false;

        if (checkTypes(expected, actual, Number.class)) {
            return function.apply((Float) expected, (Float) actual);
        }

        return false;
    }

    private static boolean eitherNull(Object expected, Object actual) {
        return null == expected || null == actual;
    }

    private static boolean checkTypes(Object expected, Object actual, Class<?> expectedType) {
        if (null == expected || null == actual || null == expectedType) {
            return false;
        }
        return Reflect.assignableFrom(expectedType, expected.getClass())
                && Reflect.assignableFrom(expectedType, actual.getClass());
    }

    private static boolean bothNull(Object expected, Object actual) {
        return null == expected && null == actual;
    }

    /**
     * Returns a list of {@link FilterTypes} which are commonly applied to {@link String} types.
     *
     * @return The list of {@link FilterTypes}
     */
    public static List<FilterType> commonStringTypes() {
        return Arrays.asList(CONTAINS, EQUALS, NOT_CONTAINS, NOT_EQUAL);
    }

    /**
     * Returns a list of {@link FilterTypes} which are commonly applied to {@link Number} types.
     *
     * @return The list of {@link FilterTypes}
     */
    public static List<FilterType> commonNumberTypes() {
        return Arrays.asList(
                GREATER_THAN, LESS_THAN, LESS_OR_EQUAL, GREATER_OR_EQUAL, NOT_EQUAL, EQUALS);
    }

    @Override
    public boolean test(Object expected, Object actual) {
        return this.defaultFilter.apply(expected, actual);
    }
}
