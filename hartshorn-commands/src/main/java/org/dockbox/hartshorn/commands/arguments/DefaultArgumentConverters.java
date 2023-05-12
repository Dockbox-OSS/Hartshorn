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

import org.dockbox.hartshorn.component.contextual.StaticBinds;
import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.i18n.TranslationService;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToBooleanConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToCharacterConverter;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToNumberConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToUUIDConverter;
import org.dockbox.hartshorn.util.option.Option;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service(permitProxying = false)
@RequiresActivator(UseCommands.class)
public class DefaultArgumentConverters {

    @StaticBinds
    public static ArgumentConverter<String> stringArgumentConverter() {
        return ArgumentConverterImpl.builder(String.class, "string")
                .withConverter((String input) -> Option.of(input))
                .build();
    }

    @StaticBinds
    public static ArgumentConverter<Character> characterArgumentConverter() {
        return ArgumentConverterImpl.builder(Character.class, "char", "character")
                .withConverter(new StringToCharacterConverter())
                .build();
    }

    @StaticBinds
    public static ArgumentConverter<Boolean> booleanArgumentConverter() {
        return ArgumentConverterImpl.builder(Boolean.class, "bool", "boolean")
                .withConverter(new StringToBooleanConverter())
                .withSuggestionProvider(in -> List.of("true", "false", "yes", "no"))
                .build();
    }

    @StaticBinds
    public static ArgumentConverter<Double> doubleArgumentConverter() {
        return ArgumentConverterImpl.builder(Double.class, "double")
                .withConverter(new StringToNumberConverterFactory().create(Double.class))
                .build();
    }

    @StaticBinds
    public static ArgumentConverter<Float> floatArgumentConverter() {
        return ArgumentConverterImpl.builder(Float.class, "float")
                .withConverter(new StringToNumberConverterFactory().create(Float.class))
                .build();
    }

    @StaticBinds
    public static ArgumentConverter<Integer> integerArgumentConverter() {
        return ArgumentConverterImpl.builder(Integer.class, "int", "integer")
                .withConverter(new StringToNumberConverterFactory().create(Integer.class))
                .build();
    }

    @StaticBinds
    public static ArgumentConverter<Long> longArgumentConverter() {
        return ArgumentConverterImpl.builder(Long.class, "long")
                .withConverter(new StringToNumberConverterFactory().create(Long.class))
                .build();
    }

    @StaticBinds
    public static ArgumentConverter<Short> shortArgumentConverter() {
        return ArgumentConverterImpl.builder(Short.class, "short")
                .withConverter(new StringToNumberConverterFactory().create(Short.class))
                .build();
    }

    @StaticBinds
    public static ArgumentConverter<UUID> uuidArgumentConverter() {
        return ArgumentConverterImpl.builder(UUID.class, "uuid", "uniqueId")
                .withConverter(new StringToUUIDConverter())
                .build();
    }

    @StaticBinds
    public static ArgumentConverter<Duration> durationArgumentConverter() {
        return ArgumentConverterImpl.builder(Duration.class, "duration")
                .withConverter(StringUtilities::durationOf)
                .build();
    }

    @StaticBinds
    public static ArgumentConverter<Message> messageArgumentConverter() {
        return ArgumentConverterImpl.builder(Message.class, "resource", "i18n", "translation")
                .withConverter((src, in) -> {
                    final TranslationService rs = src.applicationContext().get(TranslationService.class);
                    return rs.get(in);
                }).withSuggestionProvider((src, in) -> {
                    final TranslationService rs = src.applicationContext().get(TranslationService.class);
                    return rs.bundle().messages().stream()
                            .map(Message::key)
                            .filter(key -> key.toLowerCase(Locale.ROOT).startsWith(in.toLowerCase(Locale.ROOT)))
                            .collect(Collectors.toSet());
                }).build();
    }

    @StaticBinds
    public static ArgumentConverter<ComponentContainer> componentContainerArgumentConverter() {
        return ArgumentConverterImpl.builder(ComponentContainer.class, "service")
                .withConverter((src, in) -> Option.of(src.applicationContext()
                        .get(ComponentLocator.class).containers().stream()
                        .filter(container -> container.id().equalsIgnoreCase(in))
                        .findFirst()))
                .withSuggestionProvider((src, in) -> src.applicationContext()
                        .get(ComponentLocator.class).containers().stream()
                        .map(ComponentContainer::id)
                        .filter(id -> id.toLowerCase(Locale.ROOT).startsWith(in.toLowerCase(Locale.ROOT)))
                        .toList())
                .build();
    }

    @StaticBinds
    public static ArgumentConverter<String> remainingStringArgumentConverter() {
        return ArgumentConverterImpl.builder(String.class, "remaining", "remainingString")
                .withConverter((String input) -> Option.of(input))
                .withSize(-1)
                .build();
    }

    @StaticBinds
    public static ArgumentConverter<Integer[]> remainingIntegersArgumentConverter() {
        final Converter<String, Integer> integerConverter = new StringToNumberConverterFactory().create(Integer.class);
        return ArgumentConverterImpl.builder(Integer[].class, "remainingInt")
                .withConverter((String in) -> {
                    final String[] parts = in.split(" ");
                    final Integer[] integers = new Integer[parts.length];
                    for (int i = 0; i < parts.length; i++) {
                        final String part = parts[i];
                        integers[i] = integerConverter.convert(part);
                    }
                    return Option.of(integers);
                })
                .withSize(-1)
                .build();
    }
}
