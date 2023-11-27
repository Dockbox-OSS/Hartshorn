/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;

/**
 * Allows for the explicit definition of a priority for an injectable parameter. This is useful when multiple
 * implementations of the same type are available, and a specific implementation is required, or when another
 * priority should be used instead of the default resolved priority for the parameter.
 *
 * <p>Note that this annotation indicates an exact priority, and not a priority range. This means that if a
 * priority of {@code 100} is defined, and only priorities of {@code 99} and {@code 101} are available, no
 * binding will be found. It remains up to the parameter resolver to determine how to handle this situation.
 *
 * <p>For {@link Binds binding methods}, this annotation will cause a {@link ExactPriorityProviderSelectionStrategy}
 * to be used, instead of the default {@link HighestPriorityProviderSelectionStrategy}. A valid declaration
 * of this annotation on a binding method can be seen below. In this scenario, a self-type delegate is selected
 * with a priority of {@code 5}, and the binding method itself is given a priority of {@code 10}.
 *
 * <pre>{@code
 * @Binds(priority = 10)
 * public HelloWorldService helloWorldService(@Priority(5) HelloWorldService delegate) {
 *     // ...
 * }
 * }</pre>
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Priority {

    /**
     * The priority value, directly linked to an optional entry in a {@link BindingHierarchy}. Values can be
     * negative, zero or positive. The higher the value, the higher the priority.
     *
     * @return The priority value
     */
    int value();
}
