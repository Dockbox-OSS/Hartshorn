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
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.services.ComponentContainer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for components. Components are the building blocks of the framework. If a type is annotated with this
 * annotation, it is considered a component, allowing it to be processed using {@link
 * org.dockbox.hartshorn.core.services.ComponentProcessor}s.
 *
 * <p>Components are identified by their type, but it is also possible to specify additional
 * standard properties. These properties are:
 * <ul>
 *     <li>{@link #id()} - The unique identifier of the component. This is used to identify the component in the framework.
 *         If not specified, the type of the component is used to generate a valid ID through
 *         {@link org.dockbox.hartshorn.core.services.ComponentContainer#id(ApplicationContext, TypeContext)}</li>
 *     <li>{@link #name()} - The name of the component. This is used to identify the component in the framework. If not
 *         specified, the name of the class is used.</li>
 *     <li>{@link #owner()} - The owner of the component. This is typically ignored internally, but can be used by services
 *         and {@link org.dockbox.hartshorn.core.services.ComponentProcessor}s to define a synthetic hierarchy.</li>
 *     <li>{@link #singleton()} - Indicates whether a component should be treated as a singleton. When this is {@code true}
 *         there will only ever be one managed instance of the component known to the active {@link ApplicationContext}.</li>
 *     <li>{@link #type()} - The type of the component. This is used to indicate whether the component is a functional
 *         component, and thus modifiable, or if it should only be injected into.</li>
 *     <li>{@link #enabled()} - Indicates whether the component should be automatically enabled. By default, this only
 *         modifies the activation through the active {@link org.dockbox.hartshorn.core.services.ComponentLocator}.</li>
 * </ul>
 *
 * <p>The following example shows how to annotate a class as a component:
 * <pre>{@code
 *      \@Component
 *      public class MyComponent {
 *          // ...
 *      }
 * }</pre>
 *
 * @see Service
 * @see org.dockbox.hartshorn.core.services.ComponentLocator
 * @see org.dockbox.hartshorn.core.services.ComponentContainer
 * @see org.dockbox.hartshorn.core.services.ComponentProcessor
 * @see ComponentType
 *
 * @author Guus Lieben
 * @since 4.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component {

    /**
     * The unique identifier of the component. This is used to identify the component in the framework.
     * If not specified, the type of the component is used to generate a valid ID through
     * {@link org.dockbox.hartshorn.core.services.ComponentContainer#id(ApplicationContext, TypeContext)}
     *
     * @return The unique identifier of the component
     * @see ComponentContainer#id()
     */
    String id() default "";

    /**
     * The name of the component. This is used to identify the component in the framework. If not
     * specified, the name of the class is used.
     *
     * @return The name of the component
     * @see ComponentContainer#name()
     */
    String name() default "";

    /**
     * The owner of the component. This is typically ignored internally, but can be used by services
     * and {@link org.dockbox.hartshorn.core.services.ComponentProcessor}s to define a synthetic hierarchy.
     *
     * @return The owner of the component
     * @see ComponentContainer#owner()
     */
    Class<?> owner() default Void.class;

    /**
     * Indicates whether a component should be treated as a singleton. When this is {@code true}
     * there will only ever be one managed instance of the component known to the active {@link ApplicationContext}.
     *
     * @return {@code true} if the component should be treated as a singleton
     * @see ComponentContainer#singleton()
     */
    boolean singleton() default false;

    /**
     * The type of the component. This is used to indicate whether the component is a functional
     * component, and thus modifiable, or if it should only be injected into.
     *
     * @return The type of the component
     * @see ComponentContainer#type()
     */
    ComponentType type() default ComponentType.INJECTABLE;

    /**
     * Indicates whether the component should be automatically enabled. By default, this only
     * modifies the activation through the active {@link org.dockbox.hartshorn.core.services.ComponentLocator}.
     *
     * @return {@code true} if the component should be automatically enabled
     * @see ComponentContainer#enabled()
     * @deprecated No longer used, as the {@link org.dockbox.hartshorn.core.services.ComponentLocator}
     *             is responsible for enabling components.
     */
    @Deprecated(since = "4.2.5", forRemoval = true)
    boolean enabled() default true;

    /**
     * Indicates whether the component should be allowed to be proxied. Proxied components may be modified by changing
     * the behavior of the proxy.
     *
     * @return {@code true} if the component should be proxied
     * @see ComponentContainer#permitsProxying()
     */
    boolean permitProxying() default true;
}
