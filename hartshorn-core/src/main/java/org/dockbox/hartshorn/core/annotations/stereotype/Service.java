/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.core.annotations.stereotype;

import org.dockbox.hartshorn.core.ComponentType;
import org.dockbox.hartshorn.core.annotations.Extends;
import org.dockbox.hartshorn.core.annotations.activate.ServiceActivator;
import org.dockbox.hartshorn.core.proxy.Proxy;
import org.dockbox.hartshorn.core.proxy.ProxyManager;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate a component is a functional component, better known as a service. A service is a component
 * of which the methods, and thus functionality, can be directly modified through {@link ProxyManager}s to allow for
 * dynamic behavior of service definitions. This transforms the service into a {@link Proxy} instance.
 *
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
     * @see Component#lazy()
     * @return Whether the service is lazy.
     */
    boolean lazy() default false;

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

    /**
     * @see Component#requires()
     * @return The prefixes required by this service.
     */
    String[] requires() default {};
}
