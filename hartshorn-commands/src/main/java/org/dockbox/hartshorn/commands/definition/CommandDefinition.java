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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the definition of a single command or collection of {@link CommandElement elements}.
 */
@AllArgsConstructor
@Getter
public class CommandDefinition {

    /**
     * Indicates whether the definition is optional. Only applies when the
     * definition represents a collection of elements.
     */
    private final boolean optional;

    /**
     * Gets all elements contained in this definition.
     */
    private final List<CommandElement<?>> elements;

    /**
     * Gets all flags contained in this definition.
     */
    private final List<CommandFlag> flags;

}
