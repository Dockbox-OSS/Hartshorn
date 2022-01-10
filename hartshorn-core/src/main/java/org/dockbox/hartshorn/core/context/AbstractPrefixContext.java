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
import org.dockbox.hartshorn.core.CustomMultiMap;
import org.dockbox.hartshorn.core.MultiMap;
import org.dockbox.hartshorn.core.boot.ApplicationManager;
import org.dockbox.hartshorn.core.context.element.TypeContext;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;

public abstract class AbstractPrefixContext<S> extends DefaultContext implements PrefixContext {

    @Getter(AccessLevel.PROTECTED)
    private final ApplicationManager manager;

    private final Map<String, S> prefixes = new ConcurrentHashMap<>();
    private final MultiMap<Class<? extends Annotation>, Class<? extends Annotation>> annotationHierarchy = new CustomMultiMap<>(CopyOnWriteArrayList::new);

    protected AbstractPrefixContext(final ApplicationManager manager) {
        this.manager = manager;
    }

    protected abstract S process(String prefix);

    protected S get(final String prefix) {
        return this.prefixes.computeIfAbsent(prefix, this::process);
    }

    protected Collection<S> all() {
        return this.prefixes.values();
    }

    @Override
    public void prefix(final String prefix) {
        if (!this.prefixes.containsKey(prefix)) {
            this.manager().log().debug("Registering and caching prefix '%s'".formatted(prefix));
            this.prefixes.put(prefix, this.process(prefix));
        }
    }

    @Override
    public Set<String> prefixes() {
        return this.prefixes.keySet();
    }

    /**
     * Gets types decorated with a given annotation, both classes and annotationsWith. The prefix is
     * typically a package. If the annotation is present on a parent of the type, the highest level
     * member will be included.
     *
     * @param <A> The annotation constraint
     * @param annotation The annotation expected to be present on one or more types
     *
     * @return The annotated types
     */
    @Override
    public <A extends Annotation> Collection<TypeContext<?>> types(final Class<A> annotation) {
        return this.types(annotation, false);
    }

    /**
     * Gets types decorated with a given annotation, both classes and annotationsWith. The prefix is
     * typically a package. If the annotation is present on a parent of the type, it will only be
     * included if {@code skipParents} is false.
     *
     * @param <A> The annotation constraint
     * @param annotation The annotation expected to be present on one or more types
     * @param skipParents Whether to include the type if supertypes are annotated
     *
     * @return The annotated types
     */
    @Override
    public <A extends Annotation> Collection<TypeContext<?>> types(final Class<A> annotation, final boolean skipParents) {
        return this.prefixes().stream()
                .flatMap(prefix -> this.types(prefix, annotation, skipParents).stream())
                .collect(Collectors.toSet());
    }

    @Override
    public <T> Collection<TypeContext<? extends T>> children(final Class<T> type) {
        return this.children(TypeContext.of(type));
    }

    protected <A extends Annotation> Set<Class<? extends Annotation>> extensions(final Class<A> annotation) {
        if (this.annotationHierarchy.isEmpty()) {
            for (final TypeContext<? extends Annotation> annotationType : this.children(Annotation.class)) {
                for (final Class<? extends Annotation> selfOrParent : AnnotationHelper.annotationHierarchy(annotationType.type())) {
                    this.annotationHierarchy.put(selfOrParent, annotationType.type());
                }
            }
        }

        final Collection<Class<? extends Annotation>> hierarchy = this.annotationHierarchy.get(annotation);

        if (hierarchy.isEmpty()) return Set.of(annotation);
        else return Set.copyOf(hierarchy);
    }
}
