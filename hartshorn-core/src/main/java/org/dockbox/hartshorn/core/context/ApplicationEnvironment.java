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

import org.dockbox.hartshorn.core.boot.beta.HartshornApplicationManager;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.HartshornUtils;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;

public class ApplicationEnvironment {

    @Getter(AccessLevel.PACKAGE) private final PrefixContext context;
    @Getter private final boolean isCI;
    @Getter private final HartshornApplicationManager application;

    public ApplicationEnvironment(final Collection<String> prefixes, final HartshornApplicationManager application) {
        this.application = application;
        this.isCI = this.detectCI();
        this.context = new PrefixContext(prefixes, this);
        this.application().log().debug("Created new application environment (isCI: %s, prefixCount: %d)".formatted(this.isCI(), this.context().prefixes().size()));
    }

    private boolean detectCI() {
        return HartshornUtils.isCI();
    }

    public void prefix(final String prefix) {
        this.context.prefix(prefix);
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
        return this.context.types(annotation);
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
        return this.context.types(annotation, skipParents);
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
        return this.context.children(parent);
    }

    public <T> Collection<TypeContext<? extends T>> children(final Class<T> parent) {
        return this.context.children(parent);
    }

    /**
     * Gets supertypes.
     *
     * @param current
     *         the current
     *
     * @return the supertypes
     */
    public Collection<Class<?>> parents(final Class<?> current) {
        final Set<Class<?>> supertypes = HartshornUtils.emptySet();
        final Set<Class<?>> next = HartshornUtils.emptySet();
        final Class<?> superclass = current.getSuperclass();

        if (Object.class != superclass && null != superclass) {
            supertypes.add(superclass);
            next.add(superclass);
        }

        for (final Class<?> interfaceClass : current.getInterfaces()) {
            supertypes.add(interfaceClass);
            next.add(interfaceClass);
        }

        for (final Class<?> cls : next) {
            supertypes.addAll(this.parents(cls));
        }

        return supertypes;
    }

    public List<Annotation> annotationsWith(final TypeContext<?> type, final Class<? extends Annotation> annotation) {
        final List<Annotation> annotations = HartshornUtils.emptyList();
        for (final Annotation typeAnnotation : type.annotations()) {
            if (TypeContext.of(typeAnnotation.annotationType()).annotation(annotation).present()) {
                annotations.add(typeAnnotation);
            }
        }
        return annotations;
    }
}
