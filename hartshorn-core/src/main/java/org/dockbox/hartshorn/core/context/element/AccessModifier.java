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

package org.dockbox.hartshorn.core.context.element;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum AccessModifier {
    PUBLIC(Modifier::isPublic),
    PROTECTED(Modifier::isProtected),
    PRIVATE(Modifier::isPrivate),

    ABSTRACT(Modifier::isAbstract),
    FINAL(Modifier::isFinal),
    TRANSIENT(Modifier::isTransient),
    INTERFACE(Modifier::isInterface),
    NATIVE(Modifier::isNative),
    STATIC(Modifier::isStatic),
    STRICT(Modifier::isStrict),
    SYNCHRONIZED(Modifier::isSynchronized),
    VOLATILE(Modifier::isVolatile),
    ;

    public static final AccessModifier[] VALUES = AccessModifier.values();
    
    private final Predicate<Integer> predicate;

    public static List<AccessModifier> from(final int mod) {
        final List<AccessModifier> modifiers = new ArrayList<>();
        for (final AccessModifier modifier : VALUES) {
            if (modifier.predicate.test(mod)) modifiers.add(modifier);
        }
        return Collections.unmodifiableList(modifiers);
    }
}
