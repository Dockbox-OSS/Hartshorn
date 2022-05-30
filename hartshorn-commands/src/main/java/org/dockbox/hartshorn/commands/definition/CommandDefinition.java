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

package org.dockbox.hartshorn.commands.definition;

import java.util.List;

/**
 * Represents the definition of a single command or collection of {@link CommandElement elements}.
 */
public class CommandDefinition {

    private final boolean optional;
    private final List<CommandElement<?>> elements;
    private final List<CommandFlag> flags;

    public CommandDefinition(final boolean optional, final List<CommandElement<?>> elements, final List<CommandFlag> flags) {
        this.optional = optional;
        this.elements = elements;
        this.flags = flags;
    }

    /**
     * Indicates whether the definition is optional. Only applies when the
     * definition represents a collection of elements.
     */
    public boolean optional() {
        return this.optional;
    }

    /**
     * Gets all elements contained in this definition.
     */
    public List<CommandElement<?>> elements() {
        return this.elements;
    }

    /**
     * Gets all flags contained in this definition.
     */
    public List<CommandFlag> flags() {
        return this.flags;
    }
}
