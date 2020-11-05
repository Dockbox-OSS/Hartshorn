/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.util.events;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public enum FilterTypes implements FilterType {
    CONTAINS((expected, actual) -> {
        if (eitherNull(expected, actual)) return false;

        if (checkTypes(expected, actual, String.class)) {
            return ((String) actual).contains((CharSequence) expected);
        }

        return false;
    }),

    EQUALS((expected, actual) -> {
        if (bothNull(expected, actual)) return true;
        else if (eitherNull(expected, actual)) return false;

        if (expected == actual) return true;
        else return expected.equals(actual);
    }),

    GREATER_THAN((expected, actual) -> {
        return numberCheck(expected, actual, (e, a) -> e > a);
    }),

    LESS_THAN((expected, actual) -> {
        return numberCheck(expected, actual, (e, a) -> e < a);
    }),

    LESS_OR_EQUAL((expected, actual) -> {
        return numberCheck(expected, actual, (e, a) -> e <= a);
    }),

    GREATER_OR_EQUAL((expected, actual) -> {
        return numberCheck(expected, actual, (e, a) -> e >= a);
    }),

    NOT_EQUAL((expected, actual) -> !EQUALS.test(expected, actual)),

    NOT_CONTAINS((expected, actual) -> !CONTAINS.test(expected, actual)),
    ;

    private final BiFunction<Object, Object, Boolean> defaultFilter;

    private FilterTypes(BiFunction<Object, Object, Boolean> defaultFilter) {
        this.defaultFilter = defaultFilter;
    }

    @Override
    public boolean test(Object expected, Object actual) {
        return this.defaultFilter.apply(expected, actual);
    }

    private static boolean numberCheck(Object expected, Object actual, BiFunction<Float, Float, Boolean> function) {
        if (eitherNull(expected, actual)) return false;

        if (checkTypes(expected, actual, Number.class)) {
            return function.apply((Float) expected, (Float) actual);
        }

        return false;
    }

    private static boolean checkTypes(Object expected, Object actual, Class<?> expectedType) {
        if (null == expected || null == actual || null == expectedType) {
            return false;
        }
        return expectedType.isAssignableFrom(expected.getClass())
                && expectedType.isAssignableFrom(actual.getClass());
    }

    private static boolean eitherNull(Object expected, Object actual) {
        return null == expected || null == actual;
    }

    private static boolean bothNull(Object expected, Object actual) {
        return null == expected && null == actual;
    }

    public static List<FilterTypes> commonStringTypes() {
        return Arrays.asList(CONTAINS, EQUALS, NOT_CONTAINS, NOT_EQUAL);
    }

    public static List<FilterTypes> commonNumberTypes() {
        return Arrays.asList(GREATER_THAN, LESS_THAN, LESS_OR_EQUAL, GREATER_OR_EQUAL, NOT_EQUAL, EQUALS);
    }
}
