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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.component.processing.ComponentProcessor;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for components. Components are the building blocks of the framework. If a type is annotated with this
 * annotation, it is considered a component, allowing it to be processed using {@link
 * ComponentProcessor}s.
 *
 * <p>Components are identified by their type, but it is also possible to specify additional
 * standard properties. These properties are:
 * <ul>
 *     <li>{@link #id()} - The unique identifier of the component. This is used to identify the component in the framework.
 *         If not specified, the type of the component is used to generate a valid ID through
 *         {@link ComponentUtilities#id(ApplicationContext, TypeContext)}</li>
 *     <li>{@link #name()} - The name of the component. This is used to identify the component in the framework. If not
 *         specified, the name of the class is used.</li>
 *     <li>{@link #owner()} - The owner of the component. This is typically ignored internally, but can be used by services
 *         and {@link ComponentProcessor}s to define a synthetic hierarchy.</li>
 *     <li>{@link #singleton()} - Indicates whether a component should be treated as a singleton. When this is {@code true}
 *         there will only ever be one managed instance of the component known to the active {@link ApplicationContext}.</li>
 *     <li>{@link #type()} - The type of the component. This is used to indicate whether the component is a functional
 *         component, and thus modifiable, or if it should only be injected into.</li>
 * </ul>
 *
 * <p>The following example shows how to annotate a class as a component:
 * <pre>{@code
 *      @Component
 *      public class MyComponent {
 *          // ...
 *      }
 * }</pre>
 *
 * @see Service
 * @see ComponentLocator
 * @see ComponentContainer
 * @see ComponentProcessor
 * @see ComponentType
 *
 * @author Guus Lieben
 * @since 21.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component {

    /**
     * The unique identifier of the component. This is used to identify the component in the framework.
     * If not specified, the type of the component is used to generate a valid ID through
     * {@link ComponentUtilities#id(ApplicationContext, TypeContext)}
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
     * and {@link ComponentProcessor}s to define a synthetic hierarchy.
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
     * Indicates whether a component should be created after the application context has been initialized.
     * When this is {@code true} the component will be created after the application context has been
     * initialized, as long as {@link #singleton()} is {@code true}. If {@link #singleton()} is {@code false},
     * the component will always be lazy-loaded.
     *
     * @return {@code true} if the component should be created after the application context has been initialized
     * @see ComponentContainer#lazy()
     */
    boolean lazy() default true;

    /**
     * The type of the component. This is used to indicate whether the component is a functional
     * component, and thus modifiable, or if it should only be injected into.
     *
     * @return The type of the component
     * @see ComponentContainer#type()
     */
    ComponentType type() default ComponentType.INJECTABLE;

    /**
     * Indicates whether the component should be allowed to be proxied. Proxied components may be modified by changing
     * the behavior of the proxy.
     *
     * @return {@code true} if the component should be proxied
     * @see ComponentContainer#permitsProxying()
     */
    boolean permitProxying() default true;

    /**
     * Indicates whether the component should be allowed to be processed by {@link ComponentProcessor}s.
     * Processed components may be modified by changing the behavior or content of the component.
     *
     * @return {@code true} if the component should be processed
     */
    boolean permitProcessing() default true;

    /**
     * Indicates one or more prefixed types which are required to be present on the classpath in order for the component
     * to be loaded. This is used to prevent components from being loaded when they are not required. If one or more types
     * are specified, the component will only be loaded if all the types are present on the classpath. If no types
     * are specified, the component will always be loaded (assuming other conditions are met).
     *
     * <p>This requires the fully qualified class name to be specified. For example, if the class is named
     * {@code com.example.MyComponent}, the type would be {@code com.example.MyComponent}. {@code MyComponent} or
     * {@code com.example.MyComponent.class} are not valid.
     *
     * @return The required types.
     */
    String[] requires() default {};
}
