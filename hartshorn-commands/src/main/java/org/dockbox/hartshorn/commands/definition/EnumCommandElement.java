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
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EnumCommandElement<E extends Enum<E>> implements CommandElement<E> {

    private final String name;
    private final Map<String, E> values;
    private final boolean optional;

    EnumCommandElement(String name, TypeView<E> type, boolean optional) {
        this.name = name;
        this.values = type.enumConstants().stream().collect(Collectors.toMap(value -> value.name().toLowerCase(), Function.identity(), (value, value2) -> {
            throw new UnsupportedOperationException(type.qualifiedName() + " contains more than one enum constant with the same name, only differing by capitalization, which is unsupported.");
        }));
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
    public Option<E> parse(CommandSource source, String argument) {
        return Option.of(this.values.get(argument.toLowerCase()));
    }

    @Override
    public Collection<String> suggestions(CommandSource source, String argument) {
        return this.values.keySet().stream()
                .filter(value -> value.toLowerCase(Locale.ROOT).startsWith(argument.toLowerCase(Locale.ROOT)))
                .toList();
    }

    @Override
    public int size() {
        return 1;
    }

    public static <E extends Enum<E>> CommandElement<E> of(String name, TypeView<E> type, boolean optional) {
        return new EnumCommandElement<>(name, type, optional);
    }
}
