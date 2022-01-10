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

package org.dockbox.hartshorn.core.annotations.stereotype;

import org.dockbox.hartshorn.core.ComponentType;
import org.dockbox.hartshorn.core.annotations.Extends;
import org.dockbox.hartshorn.core.annotations.activate.ServiceActivator;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate a component is a functional component, better known as a service. A service is a component
 * of which the methods, and thus functionality, can be directly modified through {@link ProxyHandler}s to allow for
 * dynamic behavior of service definitions.
 *
 * <p>Services carries an additional {@link #activators()} attribute, which is used to indicate when a service should
 * become active by default.
 *
 * <p>By default all services are {@link #singleton() singletons}.
 *
 * @author Guus Lieben
 * @since 21.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Extends(Component.class)
@Component(type = ComponentType.FUNCTIONAL)
@ServiceActivator
public @interface Service {

    /**
     * @see Component#id()
     * @return The id of the service.
     */
    String id() default "";

    /**
     * @see Component#name()
     * @return The name of the service.
     */
    String name() default "";

    /**
     * @see Component#enabled()
     * @return Whether the service is enabled.
     * @deprecated See {@link Component#enabled()}
     */
    @Deprecated(since = "22.1", forRemoval = true)
    boolean enabled() default true;

    /**
     * @see Component#owner()
     * @return The owner of the service.
     */
    Class<?> owner() default Void.class;

    /**
     * @see Component#singleton()
     * @return Whether the service is a singleton.
     */
    boolean singleton() default true;

    /**
     * The activators required for this service to become active by default. If one or more activators are not present,
     * the service will not be loaded.
     *
     * @return The activators required for this service to become active by default.
     * @see ServiceActivator
     * @see org.dockbox.hartshorn.core.services.ComponentLocator
     */
    Class<? extends Annotation>[] activators() default Service.class;

    /**
     * @see Component#permitProxying()
     * @return Whether the service is permitted to be proxied.
     */
    boolean permitProxying() default true;
}
