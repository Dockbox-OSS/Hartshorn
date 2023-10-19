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

package org.dockbox.hartshorn.commands.arguments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.service.CommandParameter;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Simple implementation of {@link org.dockbox.hartshorn.commands.definition.ArgumentConverter} using
 * functions to provide converter and suggestion behavior.
 *
 * @param <T> The type this converter can convert to
 */
public final class ArgumentConverterImpl<T> extends DefaultArgumentConverter<T> {

    private final BiFunction<CommandSource, String, Option<T>> converter;
    private final BiFunction<CommandSource, String, Collection<String>> suggestionProvider;

    private ArgumentConverterImpl(Class<T> type, int size, BiFunction<CommandSource, String, Option<T>> converter, BiFunction<CommandSource, String, Collection<String>> suggestionProvider, String... keys) {
        super(type, size, keys);
        this.converter = converter;
        this.suggestionProvider = suggestionProvider;
    }

    @Override
    public Option<T> convert(CommandSource source, String argument) {
        return this.converter.apply(source, argument);
    }

    /**
     * Creates a builder with the provided type and keys, with the default converter and suggestions provider.
     *
     * @param type The type the converter should convert into.
     * @param keys The keys associated with the converter
     * @param <T> The type parameter of the type
     *
     * @return A new {@link CommandValueConverterBuilder} with the provided type and keys.
     */
    public static <T> CommandValueConverterBuilder<T> builder(Class<T> type, String... keys) {
        return new CommandValueConverterBuilder<>(type, keys);
    }

    @Override
    public Option<T> convert(CommandSource source, CommandParameter<String> value) {
        return this.convert(source, value.value());
    }

    /**
     * Builder type to create a new {@link ArgumentConverterImpl}.
     *
     * @param <T> The type the converter should convert into.
     */
    public static final class CommandValueConverterBuilder<T> {
        private final String[] keys;
        private final Class<T> type;
        private int size;
        private BiFunction<CommandSource, String, Option<T>> converter = (source, in) -> Option.empty();
        private BiFunction<CommandSource, String, Collection<String>> suggestionProvider = (source, in) -> new ArrayList<>();

        private CommandValueConverterBuilder(Class<T> type, String... keys) {
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
        public CommandValueConverterBuilder<T> withSize(int size) {
            this.size = size;
            return this;
        }

        /**
         * Sets the converter function of the converter, indicating the behavior to convert a {@link CommandSource} and
         * {@link String} into a possible value of type {@code T}.
         *
         * @param converter
         *         The converter function
         *
         * @return The existing builder instance
         */
        public CommandValueConverterBuilder<T> withConverter(BiFunction<CommandSource, String, Option<T>> converter) {
            this.converter = converter;
            return this;
        }

        public CommandValueConverterBuilder<T> withConverter(Converter<String, T> converter) {
            this.converter = (source, in) -> Option.of(converter.convert(in));
            return this;
        }

        /**
         * Sets the converter function of the converter, indicating the behavior to convert a {@link String} into a possible
         * value of type {@code T}.
         *
         * @param converter
         *         The converter function
         *
         * @return The existing builder instance
         */
        public CommandValueConverterBuilder<T> withConverter(Function<String, Option<T>> converter) {
            this.converter = (source, in) -> converter.apply(in);
            return this;
        }

        /**
         * Sets the suggestion provider of the converter, indicating how suggestions are generated based on a given {@link String}.
         *
         * @param suggestionProvider
         *         The suggestions provider
         *
         * @return The existing builder instance
         */
        public CommandValueConverterBuilder<T> withSuggestionProvider(Function<String, Collection<String>> suggestionProvider) {
            this.suggestionProvider = (source, in) -> suggestionProvider.apply(in);
            return this;
        }

        /**
         * Sets the suggestion provider of the converter, indicating how suggestions are generated based on a given {@link String} and
         * {@link CommandSource}.
         *
         * @param suggestionProvider
         *         The suggestions provider
         *
         * @return The existing builder instance
         */
        public CommandValueConverterBuilder<T> withSuggestionProvider(BiFunction<CommandSource, String, Collection<String>> suggestionProvider) {
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
    public Collection<String> suggestions(CommandSource source, String argument) {
        return this.suggestionProvider.apply(source, argument);
    }
}
