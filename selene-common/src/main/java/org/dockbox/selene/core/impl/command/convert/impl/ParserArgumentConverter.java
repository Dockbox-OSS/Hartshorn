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

import org.dockbox.selene.core.command.context.CommandValue.Argument;
import org.dockbox.selene.core.command.parse.AbstractTypeArgumentParser;
import org.dockbox.selene.core.impl.command.convert.ArgumentConverter;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.objects.targets.CommandSource;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

public class ParserArgumentConverter<T> extends ArgumentConverter<T> {

    private final Supplier<AbstractTypeArgumentParser<T>> parser;
    private final Function<String, Collection<String>> suggestionConverter;

    public ParserArgumentConverter(
            Class<T> type,
            Supplier<AbstractTypeArgumentParser<T>> parser,
            Function<String, Collection<String>> suggestionConverter,
            String... keys
    ) {
        super(type, keys);
        this.parser = parser;
        this.suggestionConverter = suggestionConverter;
    }

    @Override
    public Exceptional<T> convert(CommandSource source, String argument) {
        return Exceptional.ofNullable(
                this.parser.get()
                        .parse(new Argument<>(argument, super.getKeys().get(0)))
                        .orElse(null)
        );
    }

    @Override
    public Collection<String> getSuggestions(CommandSource source, String argument) {
        return this.suggestionConverter.apply(argument);
    }

}
