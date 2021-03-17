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

package org.dockbox.selene.command.convert;

import org.dockbox.selene.api.command.context.CommandValue;
import org.dockbox.selene.api.command.source.CommandSource;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.command.parameter.CustomParameterPattern;
import org.dockbox.selene.common.command.convert.AbstractArgumentConverter;

import java.util.Collection;

/**
 * The default converter for any type which can be constructed with a {@link CustomParameterPattern}. Typically
 * this only applies to types annotated with {@link org.dockbox.selene.annotations.command.CustomParameter},
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
        return pattern.request(getType(), source, argument);
    }

    @Override
    public Exceptional<T> convert(CommandSource source, CommandValue<String> value) {
        return pattern.request(getType(), source, value.getValue());
    }

    @Override
    public Collection<String> getSuggestions(CommandSource source, String argument) {
        return SeleneUtils.emptyList();
    }
}
