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

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.core.HartshornUtils;

import java.util.Collection;

import lombok.AllArgsConstructor;

/**
 * Simple implementation of a value-based {@link CommandFlag}. Using an underlying
 * {@link CommandElement} to delegate value parsing.
 *
 * @param <T> The type of the expected value
 */
@AllArgsConstructor
public class CommandFlagElement<T> implements CommandFlag, CommandElement<T> {

    private final CommandElement<T> element;

    @Override
    public boolean optional() {
        return true;
    }

    @Override
    public Exceptional<T> parse(final CommandSource source, final String argument) {
        return this.element.parse(source, argument);
    }

    @Override
    public Collection<String> suggestions(final CommandSource source, final String argument) {
        return this.element.suggestions(source, argument);
    }

    @Override
    public int size() {
        return this.element.size();
    }

    @Override
    public String name() {
        return HartshornUtils.trimWith('-', this.element.name());
    }

    @Override
    public boolean value() {
        return true;
    }
}
