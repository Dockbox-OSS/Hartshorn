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

package org.dockbox.hartshorn.inject.annotations.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dockbox.hartshorn.inject.provider.LifecycleType;
import org.dockbox.hartshorn.util.introspect.annotations.Extends;

import com.google.errorprone.annotations.Keep;

/**
 * Stereotype of {@link Binds} for bindings that should be registered as singletons. This annotation is a shorthand
 * for {@code @Binds(lifecycle = LifecycleType.SINGLETON)}.
 *
 * <p>Singleton bindings are only created once and are shared across all consumers. This is useful for bindings that
 * are stateless or immutable, or for bindings that are expensive to create.
 *
 * @see Binds
 * @see LifecycleType#SINGLETON
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Extends(Binds.class)
@Binds(lifecycle = LifecycleType.SINGLETON)
@Keep
public @interface Singleton {

    /**
     * Whether the binding should be lazily loaded. If not specified, the binding will be loaded eagerly.
     *
     * @return whether the binding should be lazily loaded
     *
     * @see Binds#lazy()
     */
    boolean lazy() default false;

    /**
     * Bindings that should be registered before this binding, for example if this binding depends on other
     * bindings, but doesn't require them to be provided by the container. If not specified, no guarantees are made
     * about the order in which bindings are registered, besides their priority.
     *
     * <p>Bindings declared here are considered to be dependencies of this binding. This is similar to requiring the
     * bindings as method parameters, but without the requirement to actually declare method parameters.
     *
     * @return the bindings that should be registered before this binding
     *
     * @see Binds#after()
     */
    Class<?>[] after() default {};

    /**
     * Whether the result of the binding provider should be processed by {@link ComponentPostProcessor}s after
     * it has been initialized. If not specified, the result of the binding provider will be processed after it
     * has been initialized. If {@code false}, the result of the binding provider will not be processed until
     * it is requested from the container.
     *
     * @return whether the result of the binding provider should be processed after it has been initialized
     *
     * @see Binds#processAfterInitialization()
     */
    boolean processAfterInitialization() default true;
}
