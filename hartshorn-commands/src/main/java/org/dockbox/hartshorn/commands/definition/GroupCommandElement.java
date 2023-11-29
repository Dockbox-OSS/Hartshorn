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

package org.dockbox.hartshorn.commands.definition;

import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Collection;
import java.util.List;

/**
 * Represents a group of {@link CommandElement command elements}. This is typically used when
 * a single argument is only valid in combination with another argument.
 */
public class GroupCommandElement implements CommandElement<List<CommandElement<?>>> {

    private final List<CommandElement<?>> elements;
    private final String name;
    private final boolean optional;
    private final int size;

    public GroupCommandElement(List<CommandElement<?>> elements, boolean optional) {
        this.elements = elements;
        List<String> names = elements.stream().map(CommandElement::name).toList();
        this.name = "group: " + String.join(", ", names);
        this.size = elements.stream().mapToInt(CommandElement::size).sum();
        this.optional = optional;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public boolean optional() {
        return this.optional;
    }

    @Override
    public Option<List<CommandElement<?>>> parse(CommandSource source, String argument) {
        return Option.of(this.elements);
    }

    @Override
    public Collection<String> suggestions(CommandSource source, String argument) {
        throw new UnsupportedOperationException("Collecting suggestions from element groups is not supported, target singular elements instead.");
    }

    @Override
    public int size() {
        return this.size;
    }
}
