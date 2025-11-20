/*
 * Copyright 2019-2025 the original author or authors.
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

import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.inject.condition.ConditionMatcher;

/**
 * Represents an environment that is capable of managing components.
 *
 * @see ComponentRegistry
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface ManagedComponentEnvironment extends InjectorEnvironment {

    /**
     * Returns the {@link ComponentRegistry} that is used by this
     * {@link ManagedComponentEnvironment} to locate components.
     *
     * @return the {@link ComponentRegistry}
     */
    ComponentRegistry componentRegistry();

    /**
     * Returns the {@link ConditionMatcher} that is used by this {@link ManagedComponentEnvironment}
     * to evaluate conditions.
     *
     * @return the {@link ConditionMatcher}
     */
    ConditionMatcher conditionMatcher();
}
