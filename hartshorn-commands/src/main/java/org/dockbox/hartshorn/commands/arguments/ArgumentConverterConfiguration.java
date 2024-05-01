/*
 * Copyright 2019-2024 the original author or authors.
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

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.commands.context.ArgumentConverterRegistry;
import org.dockbox.hartshorn.commands.context.ArgumentConverterRegistryCustomizer;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentRegistry;
import org.dockbox.hartshorn.component.Configuration;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.Singleton;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.i18n.TranslationService;
import org.dockbox.hartshorn.inject.SupportPriority;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToBooleanConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToCharacterConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToNumberConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToUUIDConverter;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Default configuration for command argument converters. This configuration targets any
 * {@link ArgumentConverterRegistry} that supports
 * the use of {@link ArgumentConverterRegistryCustomizer customizers}, and applies a range
 * of default converters to the registry.
 *
 * @see ArgumentConverterRegistryCustomizer
 * @see ArgumentConverter
 * @see ArgumentConverterRegistry
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
@Configuration
@RequiresActivator(UseCommands.class)
public class ArgumentConverterConfiguration {

    /**
     * Provides a customizer that registers a range of default argument converters with the
     * provided registry. Note that this only registers the converters defined in this
     * configuration, and does not include any composite members.
     *
     * @return a customizer that registers default argument converters
     */
    @Singleton
    @SupportPriority
    public ArgumentConverterRegistryCustomizer converterRegistryCustomizer() {
        return registry -> {
            registry.registerConverter(this.stringArgumentConverter());
            registry.registerConverter(this.characterArgumentConverter());
            registry.registerConverter(this.booleanArgumentConverter());
            registry.registerConverter(this.doubleArgumentConverter());
            registry.registerConverter(this.floatArgumentConverter());
            registry.registerConverter(this.integerArgumentConverter());
            registry.registerConverter(this.longArgumentConverter());
            registry.registerConverter(this.shortArgumentConverter());
            registry.registerConverter(this.uuidArgumentConverter());
            registry.registerConverter(this.durationArgumentConverter());
            registry.registerConverter(this.messageArgumentConverter());
            registry.registerConverter(this.componentContainerArgumentConverter());
            registry.registerConverter(this.remainingStringArgumentConverter());
            registry.registerConverter(this.remainingIntegersArgumentConverter());
        };
    }

    /**
     * Provides an argument converter for {@link String} arguments. This is a one-to-one
     * converter that simply wraps the input in an {@link Option} for further processing.
     *
     * @return an argument converter for {@link String} arguments
     */
    public ArgumentConverter<String> stringArgumentConverter() {
        return ArgumentConverterImpl.builder(String.class, "string")
                .withConverter((String input) -> Option.of(input))
                .build();
    }

    /**
     * Provides an argument converter for {@link Character} arguments. This converter
     * delegates to a {@link StringToCharacterConverter} for the actual conversion.
     *
     * @return an argument converter for {@link Character} arguments
     */
    public ArgumentConverter<Character> characterArgumentConverter() {
        return ArgumentConverterImpl.builder(Character.class, "char", "character")
                .withConverter(new StringToCharacterConverter())
                .build();
    }

    /**
     * Provides an argument converter for {@link Boolean} arguments. This converter
     * delegates to a {@link StringToBooleanConverter} for the actual conversion.
     *
     * @return an argument converter for {@link Boolean} arguments
     */
    public ArgumentConverter<Boolean> booleanArgumentConverter() {
        return ArgumentConverterImpl.builder(Boolean.class, "bool", "boolean")
                .withConverter(new StringToBooleanConverter())
                .withSuggestionProvider(in -> List.of("true", "false", "yes", "no"))
                .build();
    }

    /**
     * Provides an argument converter for {@link Double} arguments. This converter
     * delegates to a {@link Double} converter created through a {@link
     * StringToNumberConverterFactory} for the actual conversion.
     *
     * @return an argument converter for {@link Double} arguments
     */
    public ArgumentConverter<Double> doubleArgumentConverter() {
        return ArgumentConverterImpl.builder(Double.class, "double")
                .withConverter(new StringToNumberConverterFactory().create(Double.class))
                .build();
    }

    /**
     * Provides an argument converter for {@link Float} arguments. This converter
     * delegates to a {@link Float} converter created through a {@link
     * StringToNumberConverterFactory} for the actual conversion.
     *
     * @return an argument converter for {@link Float} arguments
     */
    public ArgumentConverter<Float> floatArgumentConverter() {
        return ArgumentConverterImpl.builder(Float.class, "float")
                .withConverter(new StringToNumberConverterFactory().create(Float.class))
                .build();
    }

    /**
     * Provides an argument converter for {@link Integer} arguments. This converter
     * delegates to a {@link Integer} converter created through a {@link
     * StringToNumberConverterFactory} for the actual conversion.
     *
     * @return an argument converter for {@link Integer} arguments
     */
    public ArgumentConverter<Integer> integerArgumentConverter() {
        return ArgumentConverterImpl.builder(Integer.class, "int", "integer")
                .withConverter(new StringToNumberConverterFactory().create(Integer.class))
                .build();
    }

    /**
     * Provides an argument converter for {@link Long} arguments. This converter
     * delegates to a {@link Long} converter created through a {@link
     * StringToNumberConverterFactory} for the actual conversion.
     *
     * @return an argument converter for {@link Long} arguments
     */
    public ArgumentConverter<Long> longArgumentConverter() {
        return ArgumentConverterImpl.builder(Long.class, "long")
                .withConverter(new StringToNumberConverterFactory().create(Long.class))
                .build();
    }

    /**
     * Provides an argument converter for {@link Short} arguments. This converter
     * delegates to a {@link Short} converter created through a {@link
     * StringToNumberConverterFactory} for the actual conversion.
     *
     * @return an argument converter for {@link Short} arguments
     */
    public ArgumentConverter<Short> shortArgumentConverter() {
        return ArgumentConverterImpl.builder(Short.class, "short")
                .withConverter(new StringToNumberConverterFactory().create(Short.class))
                .build();
    }

    /**
     * Provides an argument converter for {@link UUID} arguments. This converter
     * delegates to a {@link StringToUUIDConverter} for the actual conversion.
     *
     * @return an argument converter for {@link UUID} arguments
     */
    public ArgumentConverter<UUID> uuidArgumentConverter() {
        return ArgumentConverterImpl.builder(UUID.class, "uuid", "uniqueId")
                .withConverter(new StringToUUIDConverter())
                .build();
    }

    /**
     * Provides an argument converter for {@link Duration} arguments. This converter
     * uses {@link StringUtilities#durationOf(String)} to convert the input.
     *
     * @return an argument converter for {@link Duration} arguments
     */
    public ArgumentConverter<Duration> durationArgumentConverter() {
        return ArgumentConverterImpl.builder(Duration.class, "duration")
                .withConverter(StringUtilities::durationOf)
                .build();
    }

    /**
     * Provides an argument converter for {@link Message} arguments. This converter
     * uses the {@link TranslationService} to retrieve the message by key.
     *
     * @return an argument converter for {@link Message} arguments
     */
    public ArgumentConverter<Message> messageArgumentConverter() {
        return ArgumentConverterImpl.builder(Message.class, "resource", "i18n", "translation")
                .withConverter((src, in) -> {
                    TranslationService rs = src.applicationContext().get(TranslationService.class);
                    return rs.get(in);
                }).withSuggestionProvider((src, in) -> {
                    TranslationService rs = src.applicationContext().get(TranslationService.class);
                    return rs.bundle().messages().stream()
                            .map(Message::key)
                            .filter(key -> key.toLowerCase(Locale.ROOT).startsWith(in.toLowerCase(Locale.ROOT)))
                            .collect(Collectors.toSet());
                }).build();
    }

    /**
     * Provides an argument converter for {@link ComponentContainer} arguments. This converter
     * uses the {@link ComponentRegistry} to retrieve the container by ID.
     *
     * @return an argument converter for {@link ComponentContainer} arguments
     */
    public ArgumentConverter<ComponentContainer<?>> componentContainerArgumentConverter() {
        return ArgumentConverterImpl.<ComponentContainer<?>>builder(TypeUtils.adjustWildcards(ComponentContainer.class, Class.class), "service")
                .withConverter((src, in) -> Option.of(src.applicationContext()
                        .get(ComponentRegistry.class).containers().stream()
                        .filter(container -> container.id().equalsIgnoreCase(in))
                        .findFirst()))
                .withSuggestionProvider((src, in) -> src.applicationContext()
                        .get(ComponentRegistry.class).containers().stream()
                        .map(ComponentContainer::id)
                        .filter(id -> id.toLowerCase(Locale.ROOT).startsWith(in.toLowerCase(Locale.ROOT)))
                        .toList())
                .build();
    }

    /**
     * Provides an argument converter that consumes all remaining arguments as a single
     * {@link String}. This converter is useful for commands that accept a variable number
     * of arguments, and want to consume all remaining arguments as a single string.
     *
     * @return an argument converter that consumes all remaining arguments as a single string
     */
    public ArgumentConverter<String> remainingStringArgumentConverter() {
        return ArgumentConverterImpl.builder(String.class, "remaining", "remainingString")
                .withConverter((String input) -> Option.of(input))
                .withSize(-1)
                .build();
    }

    /**
     * Provides an argument converter that consumes all remaining arguments as an array of
     * {@link Integer}s. This converter is useful for commands that accept a variable number
     * of arguments, and want to consume all remaining arguments as an array of integers. The
     * converter delegates to a {@link StringToNumberConverterFactory} for the actual conversion.
     *
     * @return an argument converter that consumes all remaining arguments as an array of integers
     */
    public ArgumentConverter<Integer[]> remainingIntegersArgumentConverter() {
        Converter<String, Integer> integerConverter = new StringToNumberConverterFactory().create(Integer.class);
        return ArgumentConverterImpl.builder(Integer[].class, "remainingInt")
                .withConverter((String in) -> {
                    String[] parts = in.split(" ");
                    Integer[] integers = new Integer[parts.length];
                    for (int i = 0; i < parts.length; i++) {
                        String part = parts[i];
                        integers[i] = integerConverter.convert(part);
                    }
                    return Option.of(integers);
                })
                .withSize(-1)
                .build();
    }
}
