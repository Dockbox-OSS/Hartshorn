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

package org.dockbox.hartshorn.commands.arguments;

import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.annotations.Parameter;
import org.dockbox.hartshorn.commands.service.CommandParameter;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Collection;
import java.util.List;

/**
 * The default converter for any type which can be constructed with a {@link CustomParameterPattern}. Typically,
 * this only applies to types decorated with {@link Parameter},
 * however this is not a requirement.
 *
 * @param <T> The generic type
 */
public class DynamicPatternConverter<T> extends DefaultArgumentConverter<T> {

    private final CustomParameterPattern pattern;

    public DynamicPatternConverter(final Class<T> type, final CustomParameterPattern pattern, final String... keys) {
        super(type, keys);
        this.pattern = pattern;
    }

    @Override
    public Option<T> convert(final CommandSource source, final String argument) {
        return this.pattern.request(this.type(), source, argument);
    }

    @Override
    public Option<T> convert(final CommandSource source, final CommandParameter<String> value) {
        return this.pattern.request(this.type(), source, value.value());
    }

    @Override
    public Collection<String> suggestions(final CommandSource source, final String argument) {
        return List.of();
    }
}
