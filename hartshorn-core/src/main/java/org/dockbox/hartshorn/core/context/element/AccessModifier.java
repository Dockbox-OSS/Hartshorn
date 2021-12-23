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

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * The access modifier of a class, method, or other {@link ModifierCarrier}. This is a basic bitmask
 * of the {@link Modifier} constants. This enum and its contents serve as a way to easily interact with
 * the {@link Modifier} constants through {@link TypeContext}s, {@link MethodContext}s, and other
 * {@link ModifierCarrier}s.
 *
 * @author Guus Lieben
 * @since 4.2.0
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum AccessModifier {
    /**
     * @see Modifier#PUBLIC
     * @see Modifier#isPublic(int)
     */
    PUBLIC(Modifier::isPublic),
    /**
     * @see Modifier#PROTECTED
     * @see Modifier#isProtected(int)
     */
    PROTECTED(Modifier::isProtected),
    /**
     * @see Modifier#PRIVATE
     * @see Modifier#isPrivate(int)
     */
    PRIVATE(Modifier::isPrivate),

    /**
     * @see Modifier#ABSTRACT
     * @see Modifier#isAbstract(int)
     */
    ABSTRACT(Modifier::isAbstract),
    /**
     * @see Modifier#FINAL
     * @see Modifier#isFinal(int)
     */
    FINAL(Modifier::isFinal),
    /**
     * @see Modifier#TRANSIENT
     * @see Modifier#isTransient(int)
     */
    TRANSIENT(Modifier::isTransient),
    /**
     * @see Modifier#INTERFACE
     * @see Modifier#isInterface(int)
     */
    INTERFACE(Modifier::isInterface),
    /**
     * @see Modifier#NATIVE
     * @see Modifier#isNative(int)
     */
    NATIVE(Modifier::isNative),
    /**
     * @see Modifier#STATIC
     * @see Modifier#isStatic(int)
     */
    STATIC(Modifier::isStatic),
    /**
     * @see Modifier#STRICT
     * @see Modifier#isStrict(int)
     */
    STRICT(Modifier::isStrict),
    /**
     * @see Modifier#SYNCHRONIZED
     * @see Modifier#isSynchronized(int)
     */
    SYNCHRONIZED(Modifier::isSynchronized),
    /**
     * @see Modifier#VOLATILE
     * @see Modifier#isVolatile(int)
     */
    VOLATILE(Modifier::isVolatile),
    ;

    public static final AccessModifier[] VALUES = AccessModifier.values();
    
    private final Predicate<Integer> predicate;

    /**
     * Returns a list of all {@link AccessModifier}s that are set in the given {@code modifiers}.
     *
     * @param mod The modifiers to check
     * @return A list of all {@link AccessModifier}s that are set in the given {@code modifiers}.
     * @see Member#getModifiers()
     */
    public static List<AccessModifier> from(final int mod) {
        final List<AccessModifier> modifiers = new ArrayList<>();
        for (final AccessModifier modifier : VALUES) {
            if (modifier.predicate.test(mod)) modifiers.add(modifier);
        }
        return Collections.unmodifiableList(modifiers);
    }
}
