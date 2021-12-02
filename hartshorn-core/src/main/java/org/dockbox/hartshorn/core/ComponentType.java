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

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.services.ComponentContainer;
import org.dockbox.hartshorn.core.annotations.component.Component;

/**
 * Represents the type of a component, typically represented through {@link Component#type()} or
 * {@link ComponentContainer#componentType()}.
 */
public enum ComponentType {
    /**
     * Components range from POJO's to {@link org.dockbox.hartshorn.core.context.Context} types. The common property of
     * all components is that they lack functionality, and only provide context. In domain objects the provided context
     * is the entity definition, in persistent entities the context is the data represented by the entity, and in the
     * case of the {@link org.dockbox.hartshorn.core.context.ApplicationContext} it is information about all application
     * components.
     *
     * <p>Components do not require explicit identification, but can be annotated with {@link Component}. When annotated
     * with {@link Component}, the component will be registered to the active
     * {@link org.dockbox.hartshorn.core.context.ApplicationContext}.
     */
    INJECTABLE,

    /**
     * Like regular components, functional components will always allow injection. Functional components are more
     * commonly named services. Unlike regular components, services do not define context, and instead act based on
     * provided context. They can provide a wide range of functionality based on specific contexts. Services should
     * always be identified with {@link Service}, or an extension of it.
     *
     * <p>Functional components can be processed by {@link org.dockbox.hartshorn.core.services.ComponentProcessor}s and
     * be modified by {@link org.dockbox.hartshorn.core.services.ComponentModifier}s. In the case of services, the
     * {@link org.dockbox.hartshorn.core.services.ComponentProcessor} and
     * {@link org.dockbox.hartshorn.core.services.ComponentModifier} directly target service definitions.
     *
     * <p>As services are able to provide functionality based on a given context, all services are singletons by
     * default. This allows you to better optimize your application, and to ensure a state can safely be stored inside
     * services.
     */
    FUNCTIONAL,
}
