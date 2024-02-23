/*
 * Copyright 2019-2023 the original author or authors.
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

/**
 * An introspector to introspect the modifiers of an element. Modifiers include access modifiers, such as
 * {@code public} and {@code private}, as well as other modifiers, such as {@code static} and {@code final}.
 *
 * <p>This introspector may be used for any element that has modifiers, such as a class, method, or field. If
 * the element does not support all modifiers, the introspector will return {@code false} for all modifiers that
 * are not supported. For example, a method cannot be {@code transient}, so {@link #isTransient()} will always
 * return {@code false} for a method.
 *
 * <p>For more information on the modifiers, see {@link java.lang.reflect.Modifier},
 * {@link java.lang.module.ModuleDescriptor.Exports.Modifier}, and the JVM specification. Note that documentation below
 * may refer to either {@link java.lang.reflect.Modifier} or {@link java.lang.module.ModuleDescriptor.Exports.Modifier},
 * but the implementation of this introspector is not limited to use of either.
 *
 * @see java.lang.reflect.Modifier
 * @see java.lang.module.ModuleDescriptor.Exports.Modifier
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-8.html#jls-8.1.1">JLS, 8.1.1. Class Modifiers</a>
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-8.html#jls-8.3.1">JLS, 8.3.1. Field Modifiers</a>
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-8.html#jls-8.4.3">JLS, 8.4.3. Method Modifiers</a>
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-8.html#jls-8.8.3">JLS 8.8.3. Constructor modifiers</a>
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-9.html#jls-9.1.1">JLS, 9.1.1. Interface Modifiers</a>
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface ElementModifiersIntrospector {

    /**
     * Returns the modifiers of the element as an integer. This integer can be used to check for specific modifiers
     * using the {@link AccessModifier} enum. For example, to check if the element is {@code public}, you can use
     * {@code AccessModifier.PUBLIC.test(modifiers)}. This is also compatible with the {@link java.lang.reflect.Modifier}
     * class.
     *
     * @return the modifiers of the element as an integer
     */
    int asInt();

    /**
     * Checks if the element has the provided modifier. If the element does not support the modifier, {@code false}
     * is returned.
     *
     * @param modifier the modifier to check for
     * @return {@code true} if the element supports and has the provided modifier, {@code false} otherwise
     */
    boolean has(AccessModifier modifier);

    /**
     * Returns {@code true} if the element is {@code public}, {@code false} otherwise.
     *
     * @return {@code true} if the element is {@code public}, {@code false} otherwise
     *
     * @see java.lang.reflect.Modifier#isPublic(int)
     */
    boolean isPublic();

    /**
     * Returns {@code true} if the element is {@code private}, {@code false} otherwise.
     *
     * @return {@code true} if the element is {@code private}, {@code false} otherwise
     *
     * @see java.lang.reflect.Modifier#isPrivate(int)
     */
    boolean isPrivate();

    /**
     * Returns {@code true} if the element is {@code protected}, {@code false} otherwise.
     *
     * @return {@code true} if the element is {@code protected}, {@code false} otherwise
     *
     * @see java.lang.reflect.Modifier#isProtected(int)
     */
    boolean isProtected();

    /**
     * Returns {@code true} if the element is {@code static}, {@code false} otherwise.
     *
     * @return {@code true} if the element is {@code static}, {@code false} otherwise
     *
     * @see java.lang.reflect.Modifier#isStatic(int)
     */
    boolean isStatic();

    /**
     * Returns {@code true} if the element is {@code final}, {@code false} otherwise.
     *
     * @return {@code true} if the element is {@code final}, {@code false} otherwise
     *
     * @see java.lang.reflect.Modifier#isFinal(int)
     */
    boolean isFinal();

    /**
     * Returns {@code true} if the element is {@code abstract}, {@code false} otherwise.
     *
     * @return {@code true} if the element is {@code abstract}, {@code false} otherwise
     *
     * @see java.lang.reflect.Modifier#isAbstract(int)
     */
    boolean isAbstract();

    /**
     * Returns {@code true} if the element is {@code transient}, {@code false} otherwise.
     *
     * @return {@code true} if the element is {@code transient}, {@code false} otherwise
     *
     * @see java.lang.reflect.Modifier#isTransient(int)
     */
    boolean isTransient();

    /**
     * Returns {@code true} if the element is {@code volatile}, {@code false} otherwise.
     *
     * @return {@code true} if the element is {@code volatile}, {@code false} otherwise
     *
     * @see java.lang.reflect.Modifier#isVolatile(int)
     */
    boolean isVolatile();

    /**
     * Returns {@code true} if the element is {@code synchronized}, {@code false} otherwise.
     *
     * @return {@code true} if the element is {@code synchronized}, {@code false} otherwise
     *
     * @see java.lang.reflect.Modifier#isSynchronized(int)
     */
    boolean isSynchronized();

    /**
     * Returns {@code true} if the element is {@code native}, {@code false} otherwise.
     *
     * @return {@code true} if the element is {@code native}, {@code false} otherwise
     *
     * @see java.lang.reflect.Modifier#isNative(int)
     */
    boolean isNative();

    /**
     * Returns {@code true} if the element is {@code strictfp}, {@code false} otherwise.
     *
     * @return {@code true} if the element is {@code strictfp}, {@code false} otherwise
     *
     * @see java.lang.reflect.Modifier#isStrict(int)
     */
    boolean isStrict();

    /**
     * Returns {@code true} if the element is {@code mandated}, {@code false} otherwise. Note that this modifier
     * is often implicitly set by the compiler, and is not often used in code.
     *
     * @return {@code true} if the element is {@code mandated}, {@code false} otherwise
     *
     * @see java.lang.module.ModuleDescriptor.Exports.Modifier#MANDATED
     */
    boolean isMandated();

    /**
     * Returns {@code true} if the element is {@code synthetic}, {@code false} otherwise. Note that this modifier
     * is implicitly set by the compiler, and is not defined in the Java Language Specification.
     *
     * @return {@code true} if the element is {@code synthetic}, {@code false} otherwise
     *
     * @see <a href="https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.7.8">JVM Specification, 4.7.8. The Synthetic Attribute</a>
     */
    boolean isSynthetic();

    /**
     * Returns {@code true} if the element is {@code default}, {@code false} otherwise.
     *
     * @return {@code true} if the element is {@code default}, {@code false} otherwise
     *
     * @see java.lang.reflect.Method#isDefault()
     */
    boolean isDefault();
}
