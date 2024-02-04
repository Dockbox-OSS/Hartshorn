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

package org.dockbox.hartshorn.component.processing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.inject.DependencyResolver;
import org.dockbox.hartshorn.inject.Named;
import org.dockbox.hartshorn.inject.Priority;
import org.dockbox.hartshorn.inject.Qualifier;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.collection.ComponentCollection;
import org.dockbox.hartshorn.util.introspect.annotations.AttributeAlias;

import jakarta.inject.Singleton;

/**
 * Annotation used to indicate that a method will act as a binding provider. The return type of the
 * method, combined with optional {@link Qualifier} annotations form the {@link ComponentKey} of
 * the binding.
 *
 * <p>Provider methods can have parameters, which will be injected through the active
 * {@link ApplicationContext}. Depending on the active {@link DependencyResolver}, the binding provider
 * may support additional features.
 *
 * <p>If a binding provider requires additional components to be available, these can be declared as method
 * parameters. As such, a valid binding provider method signature is (for example):
 *
 * <pre>{@code
 * @Binds
 * public HelloWorldService helloWorldService(NameService nameService) {
 *   return new HelloWorldService(nameService);
 * }
 * }</pre>
 *
 * @author Guus Lieben
 * @since 0.4.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface Binds {

    /**
     * The name of the binding. If not specified, no name will be bound.
     *
     * @return the name of the binding
     *
     * @deprecated use the {@link Named} annotation or a custom {@link Qualifier} annotation instead
     */
    @Deprecated(since = "0.6.0", forRemoval = true)
    String value() default "";

    /**
     * The name of the binding. If not specified, no name will be associated with the binding.
     *
     * @return the name of the binding, or an empty string if not specified.
     */
    @Deprecated(since = "0.6.0", forRemoval = true)
    @AttributeAlias("value")
    String name() default "";

    /**
     * Retrieves the priority of the binding. If not explicitly specified, the binding will be registered with the default priority.
     *
     * <p>The priority is an integer value that determines the order in which bindings are processed. A lower value indicates
     * lower priority. If multiple bindings share the same priority, the registration order is used as a tiebreaker.
     *
     * @return The priority of the binding, or -1 if not specified.
     *
     * @deprecated use the {@link Priority} annotation instead
     */
    @Deprecated(since = "0.6.0", forRemoval = true)
    int priority() default -1;

    /**
     * Whether the binding should be lazily loaded. If not specified, the binding will be loaded eagerly. Note
     * that this only applies to bindings that are registered as singletons.
     *
     * @return whether the binding should be lazily loaded
     * @see Singleton
     */
    boolean lazy() default false;

    /**
     * Bindings that should be registered after this binding, for example to prepare configuration for these
     * bindings. If not specified, no guarantees are made about the order in which bindings are registered, besides
     * their priority.
     *
     * @return the bindings that should be registered before this binding
     */
    Class<?>[] before() default {};

    /**
     * Bindings that should be registered before this binding, for example if this binding depends on other
     * bindings, but doesn't require them to be provided by the container. If not specified, no guarantees are made
     * about the order in which bindings are registered, besides their priority.
     *
     * @return the bindings that should be registered before this binding
     */
    Class<?>[] after() default {};

    /**
     * Whether the result of the binding provider should be processed by {@link ComponentPostProcessor}s after
     * it has been initialized. If not specified, the result of the binding provider will be processed after it
     * has been initialized. If {@code false}, the result of the binding provider will not be processed until
     * it is requested from the container.
     *
     * @return whether the result of the binding provider should be processed after it has been initialized
     */
    boolean processAfterInitialization() default true;

    /**
     * The type of the binding. If not specified, the binding will be registered as a component. If specified as
     * {@link BindingType#COLLECTION}, the binding will be registered as part of a {@link ComponentCollection}.
     *
     * @return the type of the binding
     */
    BindingType type() default BindingType.STANDALONE;

    /**
     * The type of binding provider. This is used to indicate whether the binding provider is a standalone component,
     * or a part of a collection.
     */
    enum BindingType {

        /**
         * Represents a standalone component, meaning the component will be part of a direct {@link BindingHierarchy}.
         */
        STANDALONE,

        /**
         * Represents a component that is part of a collection, meaning the component will be part of a {@link ComponentCollection}.
         * Collection components are typically used to provide multiple implementations of a single interface.
         */
        COLLECTION,
    }
}
