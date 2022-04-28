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

package org.dockbox.hartshorn.inject.binding;

import org.dockbox.hartshorn.component.ComponentType;
import org.dockbox.hartshorn.util.reflect.Extends;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.inject.Key;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Named;

/**
 * Indicates the annotated type is to be bound to the specified type. This creates a basic entry
 * to the active {@link BindingHierarchy} for the specified
 * target {@link Key}. The {@link Key} is
 * created from the {@link #value()} and the {@link Named} value of {@link #named()}.
 *
 * <p>By default, the binding is created with the default priority of {@code -1}, but this can be
 * changed by specifying the {@link #priority()} value.
 *
 * <p>This annotation can be repeated, to create multiple bindings for the different
 * {@link Key}s.
 *
 * <p>This acts as a shortcut for components which implement a specific interface and wish to
 * be bound to that interface.
 *
 * @author Guus Lieben
 * @since 21.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Extends(Component.class)
@Component
public @interface ComponentBinding {

    /**
     * @see Component#id()
     */
    String id() default "";

    /**
     * @see Component#name()
     */
    String name() default "";

    /**
     * @see Component#singleton()
     */
    boolean singleton() default false;

    /**
     * @see Component#type()
     */
    ComponentType type() default ComponentType.INJECTABLE;

    /**
     * @see Component#permitProxying()
     */
    boolean permitProxying() default true;

    /**
     * The type to bind to.
     * @return The type to bind to.
     */
    Class<?> value();

    /**
     * The priority of the binding.
     * @return The priority of the binding.
     */
    int priority() default -1;

    /**
     * The {@link Named} annotation to use for the binding {@link Key}.
     * @return The {@link Named} annotation to use for the binding {@link Key}.
     */
    Named named() default @Named;
}
