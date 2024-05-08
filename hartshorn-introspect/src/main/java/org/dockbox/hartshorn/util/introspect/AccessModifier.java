/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.util.introspect;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.dockbox.hartshorn.util.introspect.view.ModifierCarrierView;

/**
 * The access modifier of a class, method, or other {@link ModifierCarrierView}. This is a basic mirror
 * of the {@link Modifier} constants. This enum and its contents serve as a way to easily interact with
 * the {@link Modifier} constants through {@link ModifierCarrierView views}, without directly interacting
 * with JDK reflections.
 *
 * @see Modifier
 * @see ModifierCarrierView
 * @see Member#getModifiers()
 *
 * @since 0.4.4
 *
 * @author Guus Lieben
 */
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

    /**
     * Static cache of all {@link AccessModifier}s, to avoid creating a new array every time
     * {@link #values()} is called.
     */
    public static final AccessModifier[] VALUES = AccessModifier.values();

    private final Predicate<Integer> predicate;

    AccessModifier(Predicate<Integer> predicate) {
        this.predicate = predicate;
    }

    /**
     * Returns whether or not this {@link AccessModifier} is set in the given {@code modifiers}.
     *
     * @param modifiers The modifiers to check
     * @return Whether or not this {@link AccessModifier} is set in the given {@code modifiers}.
     */
    public boolean test(int modifiers) {
        return this.predicate.test(modifiers);
    }

    /**
     * Returns a list of all {@link AccessModifier}s that are set in the given {@code modifiers}.
     *
     * @param mod The modifiers to check
     * @return A list of all {@link AccessModifier}s that are set in the given {@code modifiers}.
     * @see Member#getModifiers()
     */
    public static List<AccessModifier> from(int mod) {
        List<AccessModifier> modifiers = new ArrayList<>();
        for (AccessModifier modifier : VALUES) {
            if (modifier.predicate.test(mod)) {
                modifiers.add(modifier);
            }
        }
        return Collections.unmodifiableList(modifiers);
    }
}
