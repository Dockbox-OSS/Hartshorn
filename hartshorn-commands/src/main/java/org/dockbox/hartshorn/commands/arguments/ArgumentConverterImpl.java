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

import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.service.CommandParameter;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.adapter.StringTypeAdapter;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Simple implementation of {@link org.dockbox.hartshorn.commands.definition.ArgumentConverter} using
 * functions to provide converter and suggestion behavior.
 *
 * @param <T>
 *         The type this converter can convert to
 */
public final class ArgumentConverterImpl<T> extends DefaultArgumentConverter<T> {

    private final BiFunction<CommandSource, String, Exceptional<T>> converter;
    private final BiFunction<CommandSource, String, Collection<String>> suggestionProvider;

    private ArgumentConverterImpl(final TypeContext<T> type, final int size, final BiFunction<CommandSource, String, Exceptional<T>> converter, final BiFunction<CommandSource, String, Collection<String>> suggestionProvider, final String... keys) {
        super(type, size, keys);
        this.converter = converter;
        this.suggestionProvider = suggestionProvider;
    }

    @Override
    public Exceptional<T> convert(final CommandSource source, final String argument) {
        return this.converter.apply(source, argument);
    }

    /**
     * Creates a builder with the provided type and keys, with the default converter and suggestions provider.
     *
     * @param type
     *         The type the final converter should convert into.
     * @param keys
     *         The keys associated with the converter
     * @param <T>
     *         The type parameter of the type
     *
     * @return A new {@link CommandValueConverterBuilder} with the provided type and keys.
     */
    public static <T> CommandValueConverterBuilder<T> builder(final Class<T> type, final String... keys) {
        return new CommandValueConverterBuilder<>(TypeContext.of(type), keys);
    }

    @Override
    public Exceptional<T> convert(final CommandSource source, final CommandParameter<String> value) {
        return this.convert(source, value.value());
    }

    /**
     * Builder type to create a new {@link ArgumentConverterImpl}.
     *
     * @param <T>
     *         The type the converter should convert into.
     */
    public static final class CommandValueConverterBuilder<T> {
        private final String[] keys;
        private final TypeContext<T> type;
        private int size;
        private BiFunction<CommandSource, String, Exceptional<T>> converter = (source, in) -> Exceptional.empty();
        private BiFunction<CommandSource, String, Collection<String>> suggestionProvider = (source, in) -> HartshornUtils.emptyList();

        private CommandValueConverterBuilder(final TypeContext<T> type, final String... keys) {
            this.type = type;
            this.keys = keys;
            this.size = 1;
        }

        /**
         * Changes the size of the converter, indicating the amount of tokens should be consumed for this converter.
         *
         * @param size
         *         The amount of tokens to consume
         *
         * @return The existing builder instance
         */
        public CommandValueConverterBuilder<T> withSize(final int size) {
            this.size = size;
            return this;
        }

        /**
         * Sets the converter function of the converter, indicating the behavior to convert a {@link CommandSource} and
         * {@link String} into a possible value of type <code>T</code>.
         *
         * @param converter
         *         The converter function
         *
         * @return The existing builder instance
         */
        public CommandValueConverterBuilder<T> withConverter(final BiFunction<CommandSource, String, Exceptional<T>> converter) {
            this.converter = converter;
            return this;
        }

        public CommandValueConverterBuilder<T> withConverter(final StringTypeAdapter<T> adapter) {
            this.converter = (source, in) -> adapter.adapt(in);
            return this;
        }

        /**
         * Sets the converter function of the converter, indicating the behavior to convert a {@link String} into a possible
         * value of type <code>T</code>.
         *
         * @param converter
         *         The converter function
         *
         * @return The existing builder instance
         */
        public CommandValueConverterBuilder<T> withConverter(final Function<String, Exceptional<T>> converter) {
            this.converter = (source, in) -> converter.apply(in);
            return this;
        }

        /**
         * Sets the suggestions provider of the converter, indicating how suggestions are generated based on a given {@link String}.
         *
         * @param suggestionProvider
         *         The suggestions provider
         *
         * @return The existing builder instance
         */
        public CommandValueConverterBuilder<T> withSuggestionProvider(final Function<String, Collection<String>> suggestionProvider) {
            this.suggestionProvider = (source, in) -> suggestionProvider.apply(in);
            return this;
        }

        /**
         * Sets the suggestions provider of the converter, indicating how suggestions are generated based on a given {@link String} and
         * {@link CommandSource}.
         *
         * @param suggestionProvider
         *         The suggestions provider
         *
         * @return The existing builder instance
         */
        public CommandValueConverterBuilder<T> withSuggestionProvider(final BiFunction<CommandSource, String, Collection<String>> suggestionProvider) {
            this.suggestionProvider = suggestionProvider;
            return this;
        }

        /**
         * Creates a new {@link ArgumentConverterImpl} from the configured values of this builder.
         *
         * @return The new argument converter
         */
        public ArgumentConverterImpl<T> build() {
            return new ArgumentConverterImpl<>(this.type, this.size, this.converter, this.suggestionProvider, this.keys);
        }
    }

    @Override
    public Collection<String> suggestions(final CommandSource source, final String argument) {
        return this.suggestionProvider.apply(source, argument);
    }
}
