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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.inject.processing.ComponentProcessor;
import org.dockbox.hartshorn.inject.provider.LifecycleType;

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
 *         {@link ComponentUtilities#id(ApplicationContext, Class)}</li>
 *     <li>{@link #name()} - The name of the component. This is used to identify the component in the framework. If not
 *         specified, the name of the class is used.</li>
 *     <li>{@link #lifecycle()} - Indicates the lifecycle of the component. This is used to determine when the component
 *         should be created and destroyed. The default value is {@link LifecycleType#PROTOTYPE 'Prototype'}.</li>
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
 * @see ComponentRegistry
 * @see ComponentContainer
 * @see ComponentProcessor
 *
 * @author Guus Lieben
 * @since 0.4.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component {

    /**
     * The unique identifier of the component. This is used to identify the component in the framework.
     * If not specified, the type of the component is used to generate a valid ID through
     * {@link ComponentUtilities#id(ApplicationContext, Class)}
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
     * Indicates the lifecycle of the component. This is used to determine when the component should be created and destroyed.
     * The default value is {@link LifecycleType#PROTOTYPE 'Prototype'}.
     *
     * @return The lifecycle of the component
     */
    LifecycleType lifecycle() default LifecycleType.PROTOTYPE;

    /**
     * Indicates whether a component should be created after the application context has been initialized.
     * When this is {@code true} the component will be created after the application context has been
     * initialized, as long as the active {@link #lifecycle()} is {@link LifecycleType#SINGLETON 'Singleton'}.
     *
     * @return {@code true} if the component should be created after the application context has been initialized
     * @see ComponentContainer#lazy()
     */
    boolean lazy() default false;

    /**
     * The type of the component. This is used to indicate whether the component is a functional
     * component, and thus modifiable, or if it should only be injected into.
     *
     * @return The type of the component
     * @see ComponentContainer#type()
     *
     * @deprecated See {@link ComponentType}
     */
    @Deprecated(since = "0.6.0", forRemoval = true)
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

}
