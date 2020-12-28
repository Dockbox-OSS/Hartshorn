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

/**
 * Common utilities for a variety of actions. Including, but not restricted to:
 * <ul>
 *     <li>Array- and collection manipulation</li>
 *     <li>Reflections</li>
 *     <li>Strict equality</li>
 *     <li>Natural Language Processing</li>
 *     <li>Virtual randomness</li>
 *     <li>Exceptions</li>
 *     <li>Primite type assignability</li>
 *     <li>etc.</li>
 * </ul>
 *
 * <p>
 *     Utilities which are duplicated across classes should be moved to this type.
 * </p>
 */
// OverlyComplexClass: If a class has more methods than allowed by the configured threshold. In utility classes this can
//    safely be suppressed.
// unused: Several methods may not be visibly used because they are only used outside of the Core module. To avoid
//    hitting a warning limit this can be suppressed.
@SuppressWarnings({"OverlyComplexClass", "unused"})
public final class SeleneUtils {

    public static final AssertionUtil ASSERTION = new AssertionUtil();
    public static final CollectionUtil COLLECTION = new CollectionUtil();
    public static final KeyUtil KEYS = new KeyUtil();
    public static final ReflectionUtil REFLECTION = new ReflectionUtil();
    public static final MiscUtil OTHER = new MiscUtil();

    /**
     * The enum Provision.
     */
    public enum Provision {
        /**
         * Field provision.
         */
        FIELD,
        /**
         * Field name provision.
         */
        FIELD_NAME
    }

}
