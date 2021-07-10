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

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class PrefixContext {

    private final Map<String, Reflections> reflectedPrefixes = HartshornUtils.emptyConcurrentMap();

    public PrefixContext(String initialPrefix) {
        this.prefix(initialPrefix);
    }

    public void prefix(String prefix) {
        if (!this.reflectedPrefixes.containsKey(prefix))
            this.reflectedPrefixes.put(prefix, this.reflections(prefix));
    }

    private Reflections reflections(String prefix) {
        if (!this.reflectedPrefixes.containsKey(prefix)) {
            this.reflectedPrefixes.put(prefix, new Reflections(prefix));
        }
        return this.reflectedPrefixes.get(prefix);
    }

    public Set<String> prefixes() {
        return this.reflectedPrefixes.keySet();
    }

    /**
     * Gets types decorated with a given annotation, both classes and annotations. The prefix is
     * typically a package. If the annotation is present on a parent of the type, the highest level
     * member will be included.
     *
     * @param <A>
     *         The annotation constraint
     * @param annotation
     *         The annotation expected to be present on one or more types
     *
     * @return The annotated types
     */
    public <A extends Annotation> Collection<Class<?>> types(Class<A> annotation) {
        return this.types(annotation, false);
    }

    /**
     * Gets types decorated with a given annotation, both classes and annotations. The prefix is
     * typically a package. If the annotation is present on a parent of the type, it will only be
     * included if {@code skipParents} is false.
     *
     * @param <A>
     *         The annotation constraint
     * @param annotation
     *         The annotation expected to be present on one or more types
     * @param skipParents
     *         Whether or not to include the type if supertypes are annotated
     *
     * @return The annotated types
     */
    public <A extends Annotation> Collection<Class<?>> types(Class<A> annotation, boolean skipParents) {
        Set<Class<?>> types = HartshornUtils.emptySet();
        for (Reflections reflections : this.reflectedPrefixes.values()) {
            types.addAll(reflections.getTypesAnnotatedWith(annotation, !skipParents));
        }
        return HartshornUtils.asList(types);
    }

    /**
     * Gets all sub-types of a given type. The prefix is typically a package. If no sub-types exist
     * for the given type, and empty list is returned.
     *
     * @param parent
     *         The parent type to scan for subclasses
     * @param <T>
     *         The type of the parent
     *
     * @return The list of sub-types, or a empty list
     */
    public <T> Collection<Class<? extends T>> children(Class<T> parent) {
        Set<Class<? extends T>> subTypes = HartshornUtils.emptySet();
        for (Reflections reflections : this.reflectedPrefixes.values()) {
            subTypes.addAll(reflections.getSubTypesOf(parent));
        }
        return HartshornUtils.asList(subTypes);
    }

}
