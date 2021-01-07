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

package org.dockbox.selene.core.util;

import org.dockbox.selene.core.CheckedRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

@SuppressWarnings("unused")
public final class AssertionUtil {

    AssertionUtil() {}

    @Contract(value = "null -> false", pure = true)
    public boolean isNotEmpty(String value) {
        return null != value && !value.isEmpty();
    }

    @Contract(value = "null -> true", pure = true)
    public boolean isEmpty(String value) {
        return null == value || value.isEmpty();
    }

    public boolean isEmpty(Object object) {
        if (null == object) return true;
        if (object instanceof String) return this.isEmpty((String) object);
        else if (object instanceof Collection) return ((Collection<?>) object).isEmpty();
        else if (object instanceof Map) return ((Map<?, ?>) object).isEmpty();
        else if (SeleneUtils.REFLECTION.hasMethod(object, "isEmpty"))
            return SeleneUtils.REFLECTION.getMethodValue(object, "isEmpty", Boolean.class).orElse(false);
        else return false;
    }

    @Contract(pure = true)
    public boolean equals(@NonNls final String str1, @NonNls final String str2) {
        if (null == str1 || null == str2) {
            //noinspection StringEquality
            return str1 == str2;
        }
        return str1.equals(str2);
    }

    @Contract(pure = true)
    public boolean equalsIgnoreCase(@NonNls final String s1, @NonNls final String s2) {
        if (null == s1 || null == s2) {
            //noinspection StringEquality
            return s1 == s2;
        }
        return s1.equalsIgnoreCase(s2);
    }

    public boolean equalsWithTrim(@NonNls final String s1, @NonNls final String s2) {
        if (null == s1 || null == s2) {
            //noinspection StringEquality
            return s1 == s2;
        }
        return s1.trim().equals(s2.trim());
    }

    public boolean equalsIgnoreCaseWithTrim(@NonNls final String s1, @NonNls final String s2) {
        if (null == s1 || null == s2) {
            //noinspection StringEquality
            return s1 == s2;
        }
        return s1.trim().equalsIgnoreCase(s2.trim());
    }

    public boolean notEqual(Object expected, Object actual) {
        return !this.equal(expected, actual);
    }

    public boolean equal(Object expected, Object actual) {
        if (null != expected || null != actual) {
            return !(null == expected || !expected.equals(actual));
        }
        return false;
    }

    public boolean notSame(Object expected, Object actual) {
        return !this.same(expected, actual);
    }

    public boolean same(Object expected, Object actual) {
        return expected == actual;
    }

    public boolean hasContent(final String s) {
        return !(0 == SeleneUtils.OTHER.trimLength(s));    // faster than returning !isEmpty()
    }

    public boolean containsIgnoreCase(String source, String... contains) {
        String lowerSource = source.toLowerCase();
        for (String contain : contains) {
            int idx = lowerSource.indexOf(contain.toLowerCase());
            if (-1 == idx) {
                return false;
            }
            lowerSource = lowerSource.substring(idx);
        }
        return true;
    }

    @Contract("null -> true")
    public boolean isEmpty(final Object... array) {
        return null == array || 0 == Array.getLength(array);
    }


    /**
     * Returns true if a given {@link CheckedRunnable function} does not throw any type of exception when ran. Acts as
     * inverse of {@link AssertionUtil#throwsException(CheckedRunnable)}.
     *
     * @param runnable
     *         The function to run
     *
     * @return true if the function does not throw a exception
     * @see AssertionUtil#throwsException(CheckedRunnable)
     */
    public boolean doesNotThrow(CheckedRunnable runnable) {
        return !this.throwsException(runnable);
    }

    /**
     * Returns true if a given {@link CheckedRunnable function} throws any type of exception when ran.
     *
     * @param runnable
     *         The function to run
     *
     * @return true if the function throws a exception
     */
    public boolean throwsException(CheckedRunnable runnable) {
        try {
            runnable.run();
            return false;
        } catch (Throwable t) {
            return true;
        }
    }

    /**
     * Returns true if a given {@link CheckedRunnable function} does not throw a specific type of  exception when ran.
     * Acts as inverse of {@link AssertionUtil#throwsException(CheckedRunnable, Class)} )}.
     *
     * @param runnable
     *         The function to run
     * @param exception
     *         The expected type of exception
     *
     * @return true if the function does not throw a exception
     * @see AssertionUtil#throwsException(CheckedRunnable, Class)
     */
    public boolean doesNotThrow(CheckedRunnable runnable, Class<? extends Throwable> exception) {
        return !this.throwsException(runnable, exception);
    }

    /**
     * Returns true if a given {@link CheckedRunnable function} throws a specific type of exception when ran.
     *
     * @param runnable
     *         The function to run
     * @param exception
     *         The expected type of exception
     *
     * @return true if the function throws the expected exception
     */
    public boolean throwsException(CheckedRunnable runnable, Class<? extends Throwable> exception) {
        try {
            runnable.run();
            return false;
        } catch (Throwable t) {
            return SeleneUtils.REFLECTION.isAssignableFrom(exception, t.getClass());
        }
    }
}
