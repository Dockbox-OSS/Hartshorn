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

package org.dockbox.hartshorn.inject.graph.strategy;

import org.dockbox.hartshorn.inject.processing.ProcessingPriority;

/**
 * Priority levels for {@link DependencyContextResolver binding strategies}. Unlike {@link ProcessingPriority processing
 * priorities}, binding strategies do not allow for fine-grained control over the order in which they are
 * executed. Instead, they are grouped into five distinct priority levels, which are used to determine
 * the order in which binding strategies are applied to a component graph.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public enum BindingStrategyPriority {
    LOWEST(-256),
    LOW(-128),
    MEDIUM(0),
    HIGH(128),
    HIGHEST(256),
    ;

    private final int priority;

    BindingStrategyPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Get the integer value of this priority level.
     *
     * @return the integer value of this priority level
     */
    public int priority() {
        return this.priority;
    }
}
