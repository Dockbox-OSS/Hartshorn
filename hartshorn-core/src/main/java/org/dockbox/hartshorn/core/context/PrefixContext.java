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

package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.AnnotationHelper;
import org.dockbox.hartshorn.core.ArrayListMultiMap;
import org.dockbox.hartshorn.core.MultiMap;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;

public class PrefixContext extends DefaultContext {

    @Getter(AccessLevel.PROTECTED)
    private final ApplicationEnvironment environment;

    private final Map<String, Reflections> reflectedPrefixes = HartshornUtils.emptyConcurrentMap();
    private final MultiMap<Class<? extends Annotation>, Class<? extends Annotation>> annotationHierarchy = new ArrayListMultiMap<>();

    public PrefixContext(final Iterable<String> initialPrefixes, final ApplicationEnvironment environment) {
        this.environment = environment;
        for (final String initialPrefix : initialPrefixes) {
            this.prefix(initialPrefix);
        }
    }

    public void prefix(final String prefix) {
        if (!this.reflectedPrefixes.containsKey(prefix)) {
            this.environment.application().log().debug("Registering and caching prefix '%s'".formatted(prefix));
            this.reflectedPrefixes.put(prefix, this.reflections(prefix));
        }
    }

    void reset() {
        this.reflectedPrefixes.clear();
        this.annotationHierarchy.clear();
    }

    private Reflections reflections(final String prefix) {
        if (!this.reflectedPrefixes.containsKey(prefix)) {
            this.reflectedPrefixes.put(prefix, new Reflections(prefix));
        }
        return this.reflectedPrefixes.get(prefix);
    }

    public Set<String> prefixes() {
        return this.reflectedPrefixes.keySet();
    }

    /**
     * Gets types decorated with a given annotation, both classes and annotationsWith. The prefix is
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
    public <A extends Annotation> Collection<TypeContext<?>> types(final Class<A> annotation) {
        return this.types(annotation, false);
    }

    /**
     * Gets types decorated with a given annotation, both classes and annotationsWith. The prefix is
     * typically a package. If the annotation is present on a parent of the type, it will only be
     * included if {@code skipParents} is false.
     *
     * @param <A>
     *         The annotation constraint
     * @param annotation
     *         The annotation expected to be present on one or more types
     * @param skipParents
     *         Whether to include the type if supertypes are annotated
     *
     * @return The annotated types
     */
    public <A extends Annotation> Collection<TypeContext<?>> types(final Class<A> annotation, final boolean skipParents) {
        final Set<TypeContext<?>> types = HartshornUtils.emptySet();
        final Set<Class<? extends Annotation>> extensions = this.extensions(annotation);

        for (final Reflections reflections : this.reflectedPrefixes.values()) {
            for (final Class<? extends Annotation> extension : extensions) {
                for (final Class<?> type : reflections.getTypesAnnotatedWith(extension, !skipParents)) {
                    types.add(TypeContext.of(type));
                }
            }
        }
        return HartshornUtils.asList(types);
    }

    private <A extends Annotation> Set<Class<? extends Annotation>> extensions(final Class<A> annotation) {
        if (this.annotationHierarchy.isEmpty()) {
            for (final TypeContext<? extends Annotation> annotationType : this.children(Annotation.class)) {
                for (final Class<? extends Annotation> selfOrParent : AnnotationHelper.annotationHierarchy(annotationType.type())) {
                    this.annotationHierarchy.put(selfOrParent, annotationType.type());
                }
            }
        }

        final Collection<Class<? extends Annotation>> hierarchy = this.annotationHierarchy.get(annotation);

        if (hierarchy.isEmpty()) return HartshornUtils.asUnmodifiableSet(annotation);
        else return HartshornUtils.asUnmodifiableSet(hierarchy);
    }

    public <T> Collection<TypeContext<? extends T>> children(final Class<T> type) {
        return this.children(TypeContext.of(type));
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
     * @return The list of sub-types, or an empty list
     */
    public <T> Collection<TypeContext<? extends T>> children(final TypeContext<T> parent) {
        final Set<Class<? extends T>> subTypes = HartshornUtils.emptySet();
        for (final Reflections reflections : this.reflectedPrefixes.values()) {
            subTypes.addAll(reflections.getSubTypesOf(parent.type()));
        }
        return HartshornUtils.asList(subTypes).stream().map(TypeContext::of).collect(Collectors.toList());
    }


}
