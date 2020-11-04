/*
 *  Copyright (C) 2020 Guus Lieben
 *  
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *  
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.impl.command.convert.impl;

import org.dockbox.selene.core.impl.command.convert.ArgumentConverter;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.objects.targets.CommandSource;

import java.util.Collection;
import java.util.function.BiFunction;

public class SourceAwareArgumentConverter<T> extends ArgumentConverter<T> {

    private final BiFunction<CommandSource, String, Exceptional<T>> converter;
    private final BiFunction<CommandSource, String, Collection<String>> suggestionConverter;

    public SourceAwareArgumentConverter(
            Class<T> type,
            BiFunction<CommandSource, String, Exceptional<T>> converter,
            BiFunction<CommandSource, String, Collection<String>> suggestionConverter,
            String... keys
    ) {
        super(type, keys);
        this.converter = converter;
        this.suggestionConverter = suggestionConverter;
    }

    @Override
    public Exceptional<T> convert(CommandSource source, String argument) {
        return this.converter.apply(source, argument);
    }

    @Override
    public Collection<String> getSuggestions(CommandSource source, String argument) {
        return this.suggestionConverter.apply(source,argument);
    }
}
