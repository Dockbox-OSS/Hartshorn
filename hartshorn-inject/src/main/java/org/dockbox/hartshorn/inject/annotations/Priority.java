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

package org.dockbox.hartshorn.inject.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.provider.selection.ExactPriorityProviderSelectionStrategy;
import org.dockbox.hartshorn.inject.provider.selection.HighestPriorityProviderSelectionStrategy;

/**
 * Allows for the explicit definition of a priority for an injectable parameter or binding method.
 *
 * <p><h2>Methods</h2>
 * Binding methods default to the {@link Priority#DEFAULT_PRIORITY default priority} of {@code -1} if no priority is
 * defined. When a binding method requires an existing binding to be overwritten, configuring a higher priority is the
 * recommended approach.
 *
 * <p><h2>Parameters</h2>
 * For parameters this is useful when multiple implementations of the same type are available, and a specific
 * implementation is preferred, or when another priority should be used instead of the default resolved priority for
 * the parameter.
 *
 * <p>For binding methods, this annotation may cause a {@link ExactPriorityProviderSelectionStrategy} to be used,
 * instead of the default {@link HighestPriorityProviderSelectionStrategy}. A valid declaration of this annotation on
 * a binding method can be seen below. In this scenario, a self-type delegate is selected with a priority of {@code 5},
 * and the binding method itself is given a priority of {@code 10}.
 *
 * <p><h2>Notes</h2>
 * Note that this annotation indicates an exact priority, and not a priority range. This means that if a
 * priority of {@code 100} is defined, and only priorities of {@code 99} and {@code 101} are available, no
 * binding will be found. In the case of parameters it remains up to the parameter resolver to determine how to handle
 * this situation.
 *
 * <pre>{@code
 * @Singleton
 * @Priority(10)
 * public HelloWorldService helloWorldService(@Priority(5) HelloWorldService delegate) {
 *     // ...
 * }
 * }</pre>
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Priority {

    /**
     * The default priority value, used when no priority is defined. This usually indicates
     * that a component is a major part of the application, but not a core component.
     */
    int DEFAULT_PRIORITY = -1;

    /**
     * The support priority value, used for components that are not part of the core application,
     * but are required for the application to function properly.
     */
    int SUPPORT_PRIORITY = -32;

    /**
     * The infrastructure priority value, used for components that are part of the core application,
     * and are required for the application to function properly.
     */
    int INFRASTRUCTURE_PRIORITY = -64;

    /**
     * The priority value, directly linked to an optional entry in a {@link BindingHierarchy}. Values can be
     * negative, zero or positive. The higher the value, the higher the priority.
     *
     * @return The priority value
     */
    int value();
}
