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

package org.dockbox.selene.core.util;

import java.util.function.Function;

/**
 * Wraps all utility classes to a common accessor. This way all {@code final} utility classes can be accessed at once
 * and indexed more easily.
 */
@SuppressWarnings("unused")
public final class SeleneUtils {

    public static final AssertionUtil ASSERTION = new AssertionUtil();
    public static final CollectionUtil COLLECTION = new CollectionUtil();
    public static final KeyUtil KEYS = new KeyUtil();
    public static final ReflectionUtil REFLECTION = new ReflectionUtil();
    public static final MiscUtil OTHER = new MiscUtil();
    public static final InjectUtil INJECT = new InjectUtil();

    private SeleneUtils() {}

    /**
     * Common enumeration of processed field information in {@link ReflectionUtil#tryCreateFromProcessed(Class, Function, boolean) tryCreate}.
     */
    public enum Provision {
        /**
         * Uses the field name to process field information.
         */
        FIELD,
        /**
         * Uses the field to process field information.
         */
        FIELD_NAME
    }

}
