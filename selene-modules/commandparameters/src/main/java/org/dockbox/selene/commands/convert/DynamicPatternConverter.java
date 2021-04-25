/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.commands.convert;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.commands.annotations.CustomParameter;
import org.dockbox.selene.commands.parameter.CustomParameterPattern;
import org.dockbox.selene.commands.context.CommandParameter;
import org.dockbox.selene.commands.source.CommandSource;
import org.dockbox.selene.util.SeleneUtils;

import java.util.Collection;

/**
 * The default converter for any type which can be constructed with a {@link CustomParameterPattern}. Typically
 * this only applies to types annotated with {@link CustomParameter},
 * however this is not a requirement.
 *
 * @param <T> The generic type
 */
public class DynamicPatternConverter<T> extends AbstractArgumentConverter<T> {

    private final CustomParameterPattern pattern;

    public DynamicPatternConverter(Class<T> type, CustomParameterPattern pattern, String... keys) {
        super(type, keys);
        this.pattern = pattern;
    }

    @Override
    public Exceptional<T> convert(CommandSource source, String argument) {
        return this.pattern.request(this.getType(), source, argument);
    }

    @Override
    public Exceptional<T> convert(CommandSource source, CommandParameter<String> value) {
        return this.pattern.request(this.getType(), source, value.getValue());
    }

    @Override
    public Collection<String> getSuggestions(CommandSource source, String argument) {
        return SeleneUtils.emptyList();
    }
}
