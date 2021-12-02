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

import org.dockbox.hartshorn.core.annotations.UseBootstrap;
import org.dockbox.hartshorn.core.annotations.proxy.UseProxying;
import org.dockbox.hartshorn.core.boot.ApplicationManager;
import org.dockbox.hartshorn.core.context.element.TypeContext;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

/**
 * The environment of an active application. The environment is managed by a {@link ApplicationManager} and can be
 * responsible for multiple {@link ApplicationContext}s, though typically only one {@link ApplicationContext} is bound
 * to the {@link ApplicationEnvironment}.
 */
public interface ApplicationEnvironment {

    /**
     * Gets the context of all registered prefixes. This context is responsible for keeping track of known prefixes,
     * and the components known within those prefixes.
     * @return The context of all registered prefixes
     */
    PrefixContext prefixContext();

    /**
     * Indicates whether the current environment exists within a Continuous Integration environment. If this returns
     * <code>true</code> this indicates the application is not active in a production environment. For example, the
     * default test suite for the framework will indicate the environment acts as a CI environment.
     *
     * @return <code>true</code> if the environment is a CI environment, <code>false</code> otherwise.
     */
    boolean isCI();

    /**
     * Gets the {@link ApplicationManager} responsible for managing this environment.
     *
     * @return The {@link ApplicationManager} responsible for managing this environment.
     */
    ApplicationManager manager();

    /**
     * Registers the given prefix, allowing it to be indexed for components.
     *
     * @param prefix The prefix to register.
     */
    void prefix(String prefix);

    /**
     * Gets types decorated with a given annotation, both classes and annotations.
     *
     * @param <A> The annotation constraint
     * @param annotation The annotation expected to be present on one or more types
     * @return The annotated types
     */
    <A extends Annotation> Collection<TypeContext<?>> types(final Class<A> annotation);

    /**
     * Gets types decorated with a given annotation, both classes and annotations. The prefix is typically a package.
     * If <code>skipParents</code> is true, the type will only be included if it is annotated directly.
     *
     * @param prefix The prefix to scan for annotated types
     * @param annotation The annotation expected to be present on one or more types
     * @param skipParents Whether to skip the parent types
     * @param <A> The annotation constraint
     * @return The annotated types
     */
    <A extends Annotation> Collection<TypeContext<?>> types(final String prefix, final Class<A> annotation, final boolean skipParents);

    /**
     * Gets types decorated with a given annotation, both classes and annotations. If <code>skipParents</code> is
     * true, the type will only be included if it is annotated directly.
     *
     * @param <A> The annotation constraint
     * @param annotation The annotation expected to be present on one or more types
     * @param skipParents Whether to include the type if supertypes are annotated
     * @return The annotated types
     */
    <A extends Annotation> Collection<TypeContext<?>> types(final Class<A> annotation, final boolean skipParents);

    /**
     * Gets all sub-types of a given type. The prefix is typically a package. If no sub-types exist for the given type,
     * and empty list is returned.
     *
     * @param parent The parent type to scan for subclasses
     * @param <T> The type of the parent
     * @return The list of sub-types, or an empty list
     */
    <T> Collection<TypeContext<? extends T>> children(final TypeContext<T> parent);

    /**
     * Gets all sub-types of a given type. The prefix is typically a package. If no sub-types exist for the given type,
     * and empty list is returned.
     *
     * @param parent The parent type to scan for subclasses
     * @param <T> The type of the parent
     * @return The list of sub-types, or an empty list
     */
    <T> Collection<TypeContext<? extends T>> children(final Class<T> parent);

    /**
     * Gets annotations of the given type, which are decorated with the given annotation. For example, if the given
     * annotation is {@link org.dockbox.hartshorn.core.annotations.service.ServiceActivator} on the application
     * activator, the results will include all service activators like {@link UseBootstrap} and {@link UseProxying}.
     *
     * @param type The type to scan for annotations
     * @param annotation The annotation expected to be present on zero or more annotations
     * @return The annotated annotations
     */
    List<Annotation> annotationsWith(final TypeContext<?> type, final Class<? extends Annotation> annotation);
}
