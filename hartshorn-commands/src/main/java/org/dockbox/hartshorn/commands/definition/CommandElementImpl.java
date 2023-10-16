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

/**
 * Simple implementation of {@link CommandElement}.
 *
 * @param <T>
 */
public class CommandElementImpl<T> implements CommandElement<T> {

    private final ArgumentConverter<T> converter;
    private final String name;
    private final boolean optional;
    private final int size;

    public CommandElementImpl(ArgumentConverter<T> converter, String name, boolean optional, int size) {
        this.converter = converter;
        this.name = name;
        this.optional = optional;
        this.size = size;
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
    public Option<T> parse(CommandSource source, String argument) {
        return this.converter.convert(source, argument);
    }

    @Override
    public Collection<String> suggestions(CommandSource source, String argument) {
        return this.converter.suggestions(source, argument);
    }

    @Override
    public int size() {
        return this.size;
    }

}
