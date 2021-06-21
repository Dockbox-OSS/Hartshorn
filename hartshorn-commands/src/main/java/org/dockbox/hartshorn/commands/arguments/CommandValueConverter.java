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

package org.dockbox.hartshorn.commands.arguments;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.commands.service.CommandParameter;
import org.dockbox.hartshorn.commands.source.CommandSource;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CommandValueConverter<T> extends AbstractArgumentConverter<T> {

    private final BiFunction<CommandSource, String, Exceptional<T>> converter;
    private final Function<String, Collection<String>> suggestionProvider;

    @Override
    public Exceptional<T> convert(CommandSource source, String argument) {
        return this.converter.apply(source, argument);
    }

    @Override
    public Exceptional<T> convert(CommandSource source, CommandParameter<String> value) {
        return this.convert(source, value.getValue());
    }

    @Override
    public Collection<String> suggestions(CommandSource source, String argument) {
        return this.suggestionProvider.apply(argument);
    }

    private CommandValueConverter(Class<T> type, int size, BiFunction<CommandSource, String, Exceptional<T>> converter, Function<String, Collection<String>> suggestionProvider, String... keys) {
        super(type, size, keys);
        this.converter = converter;
        this.suggestionProvider = suggestionProvider;
    }

    public static <T> CommandValueConverterBuilder<T> builder(Class<T> type, String... keys) {
        return new CommandValueConverterBuilder<>(type, keys);
    }

    public static final class CommandValueConverterBuilder<T> {
        private String[] keys;
        private Class<T> type;
        private int size;
        private BiFunction<CommandSource, String, Exceptional<T>> converter;
        private Function<String, Collection<String>> suggestionProvider;

        private CommandValueConverterBuilder(Class<T> type, String... keys) {
            this.type = type;
            this.keys = keys;
            this.size = 1;
        }

        public CommandValueConverterBuilder<T> withKeys(String[] keys) {
            this.keys = keys;
            return this;
        }

        public CommandValueConverterBuilder<T> withType(Class<T> type) {
            this.type = type;
            return this;
        }

        public CommandValueConverterBuilder<T> withSize(int size) {
            this.size = size;
            return this;
        }

        public CommandValueConverterBuilder<T> withConverter(BiFunction<CommandSource, String, Exceptional<T>> converter) {
            this.converter = converter;
            return this;
        }

        public CommandValueConverterBuilder<T> withConverter(Function<String, Exceptional<T>> converter) {
            this.converter = (source, in) -> converter.apply(in);
            return this;
        }

        public CommandValueConverterBuilder<T> withSuggestionProvider(Function<String, Collection<String>> suggestionProvider) {
            this.suggestionProvider = suggestionProvider;
            return this;
        }

        public CommandValueConverter<T> build() {
            return new CommandValueConverter<>(this.type, this.size, this.converter, this.suggestionProvider, this.keys);
        }
    }
}
