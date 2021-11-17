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

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.boot.ApplicationManager;
import org.dockbox.hartshorn.core.context.element.TypeContext;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import lombok.Getter;

public class HartshornApplicationEnvironment implements ApplicationEnvironment {

    @Getter private final PrefixContext prefixContext;
    @Getter private final boolean isCI;
    @Getter private final ApplicationManager manager;

    public HartshornApplicationEnvironment(final Collection<String> prefixes, final ApplicationManager manager) {
        this.manager = manager;
        this.isCI = HartshornUtils.isCI();
        this.prefixContext = new PrefixContext(prefixes, this);
        this.manager().log().debug("Created new application environment (isCI: %s, prefixCount: %d)".formatted(this.isCI(), this.prefixContext().prefixes().size()));
    }

    @Override
    public void prefix(final String prefix) {
        this.prefixContext.prefix(prefix);
    }

    @Override
    public <A extends Annotation> Collection<TypeContext<?>> types(final Class<A> annotation) {
        return this.prefixContext.types(annotation);
    }

    @Override
    public <A extends Annotation> Collection<TypeContext<?>> types(final String prefix, final Class<A> annotation, final boolean skipParents) {
        return this.prefixContext.types(prefix, annotation, skipParents);
    }

    @Override
    public <A extends Annotation> Collection<TypeContext<?>> types(final Class<A> annotation, final boolean skipParents) {
        return this.prefixContext.types(annotation, skipParents);
    }

    @Override
    public <T> Collection<TypeContext<? extends T>> children(final TypeContext<T> parent) {
        return this.prefixContext.children(parent);
    }

    @Override
    public <T> Collection<TypeContext<? extends T>> children(final Class<T> parent) {
        return this.prefixContext.children(parent);
    }

    @Override
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

    @Override
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
