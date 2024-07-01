/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dockbox.hartshorn.inject.provider.LifecycleType;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.util.introspect.annotations.Extends;

/**
 * Annotation to indicate a component is a functional component, better known as a service. A service is a component
 * of which the methods, and thus functionality, can be directly modified through {@link ProxyManager}s to allow for
 * dynamic behavior of service definitions. This transforms the service into a {@link Proxy} instance.
 *
 * <p>By default all services exist with a {@link LifecycleType#SINGLETON 'singleton'} lifecycle, meaning that only one
 * instance of the service is created and shared throughout the application.
 *
 * @author Guus Lieben
 * @since 0.4.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Extends(Component.class)
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
     * @see Component#lifecycle()
     * @return The lifecycle of the component
     */
    LifecycleType lifecycle() default LifecycleType.SINGLETON;

    /**
     * @see Component#lazy()
     * @return Whether the service is lazy.
     */
    boolean lazy() default false;

    /**
     * @see Component#permitProxying()
     * @return Whether the service is permitted to be proxied.
     */
    boolean permitProxying() default true;
}
