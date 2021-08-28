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

import java.util.Map;

@Deprecated
@SuppressWarnings({ "unused", "OverlyComplexClass" })
public final class Reflect {

    private static final Map<Class<?>, Class<?>> primitiveWrapperMap = HartshornUtils.ofEntries(
            HartshornUtils.entry(boolean.class, Boolean.class),
            HartshornUtils.entry(byte.class, Byte.class),
            HartshornUtils.entry(char.class, Character.class),
            HartshornUtils.entry(double.class, Double.class),
            HartshornUtils.entry(float.class, Float.class),
            HartshornUtils.entry(int.class, Integer.class),
            HartshornUtils.entry(long.class, Long.class),
            HartshornUtils.entry(short.class, Short.class));
    private Reflect() {}

    /**
     * Returns true if {@code to} is equal to-, a super type of, or a primitive wrapper of {@code
     * from}.
     *
     * <p>Primitive wrappers include all JDK wrappers for native types (int, char, double, etc). E.g.
     * all of the following assignabilities return true:
     *
     * <pre>{@code
     * HartshornUtils.assigns(int.class, Integer.class);
     * HartshornUtils.assigns(Integer.class, int.class);
     * HartshornUtils.assigns(int.class, int.class);
     * HartshornUtils.assigns(Number.class, Integer.class);
     *
     * }</pre>
     *
     * @param to
     *         The possible (super) type or primite wrapper of {@code from}
     * @param from
     *         The type to compare assignability against
     *
     * @return true if {@code to} is equal to-, a super type of, or a primitive wrapper of {@code
     *         from}
     * @see Reflect#primitive(Class, Class)
     */
    public static boolean assigns(final Class<?> to, final Class<?> from) {
        if (null == to || null == from) return false;
        //noinspection ConstantConditions
        if (to == from || to.equals(from)) return true;

        if (to.isAssignableFrom(from)) {
            return true;
        }
        if (from.isPrimitive()) {
            return Reflect.primitive(to, from);
        }
        if (to.isPrimitive()) {
            return Reflect.primitive(from, to);
        }
        return false;
    }

    /**
     * Returns true if {@code targetClass} is a primitive wrapper of {@code primitive}.
     *
     * @param targetClass
     *         The primitive wrapper (e.g. Integer)
     * @param primitive
     *         The primitive type (e.g. int)
     *
     * @return true if {@code targetClass} is a primitive wrapper of {@code primitive}.
     */
    public static boolean primitive(final Class<?> targetClass, final Class<?> primitive) {
        if (!primitive.isPrimitive()) {
            throw new IllegalArgumentException("First argument has to be primitive type");
        }
        return primitiveWrapperMap.get(primitive) == targetClass;
    }
}
