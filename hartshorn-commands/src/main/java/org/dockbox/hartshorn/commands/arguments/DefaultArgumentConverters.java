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

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.beans.Bean;
import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.i18n.TranslationService;
import org.dockbox.hartshorn.util.BuiltInStringTypeAdapters;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.Vector3N;
import org.dockbox.hartshorn.util.option.Option;

@Service(permitProxying = false)
@RequiresActivator(UseCommands.class)
public final class DefaultArgumentConverters {

    @Bean
    public static ArgumentConverter<String> stringArgumentConverter() {
        return ArgumentConverterImpl.builder(String.class, "string")
                .withConverter(BuiltInStringTypeAdapters.STRING)
                .build();
    }

    @Bean
    public static ArgumentConverter<Character> characterArgumentConverter() {
        return ArgumentConverterImpl.builder(Character.class, "char", "character")
                .withConverter(BuiltInStringTypeAdapters.CHARACTER)
                .build();
    }

    @Bean
    public static ArgumentConverter<Boolean> booleanArgumentConverter() {
        return ArgumentConverterImpl.builder(Boolean.class, "bool", "boolean")
                .withConverter(BuiltInStringTypeAdapters.BOOLEAN)
                .withSuggestionProvider(in -> List.of("true", "false", "yes", "no"))
                .build();
    }

    @Bean
    public static ArgumentConverter<Double> doubleArgumentConverter() {
        return ArgumentConverterImpl.builder(Double.class, "double")
                .withConverter(BuiltInStringTypeAdapters.DOUBLE)
                .build();
    }

    @Bean
    public static ArgumentConverter<Float> floatArgumentConverter() {
        return ArgumentConverterImpl.builder(Float.class, "float")
                .withConverter(BuiltInStringTypeAdapters.FLOAT)
                .build();
    }

    @Bean
    public static ArgumentConverter<Integer> integerArgumentConverter() {
        return ArgumentConverterImpl.builder(Integer.class, "int", "integer")
                .withConverter(BuiltInStringTypeAdapters.INTEGER)
                .build();
    }

    @Bean
    public static ArgumentConverter<Long> longArgumentConverter() {
        return ArgumentConverterImpl.builder(Long.class, "long")
                .withConverter(BuiltInStringTypeAdapters.LONG)
                .build();
    }

    @Bean
    public static ArgumentConverter<Short> shortArgumentConverter() {
        return ArgumentConverterImpl.builder(Short.class, "short")
                .withConverter(BuiltInStringTypeAdapters.SHORT)
                .build();
    }

    @Bean
    public static ArgumentConverter<UUID> uuidArgumentConverter() {
        return ArgumentConverterImpl.builder(UUID.class, "uuid", "uniqueId")
                .withConverter(BuiltInStringTypeAdapters.UNIQUE_ID)
                .build();
    }

    @Bean
    public static ArgumentConverter<Vector3N> vector3NArgumentConverter() {
        return ArgumentConverterImpl.builder(Vector3N.class, "vec3", "vector", "v3n")
                .withConverter(in -> Option.of(
                        () -> {
                            final String[] xyz = in.split(",");
                            // IndexOutOfBounds is caught by Callable handle in Result
                            final double x = Double.parseDouble(xyz[0].trim());
                            final double y = Double.parseDouble(xyz[1].trim());
                            final double z = Double.parseDouble(xyz[2].trim());
                            return Vector3N.of(x, y, z);
                        }))
                .build();
    }

    @Bean
    public static ArgumentConverter<Duration> durationArgumentConverter() {
        return ArgumentConverterImpl.builder(Duration.class, "duration")
                .withConverter(StringUtilities::durationOf)
                .build();
    }

    @Bean
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

    @Bean
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

    @Bean
    public static ArgumentConverter<String> remainingStringArgumentConverter() {
        return ArgumentConverterImpl.builder(String.class, "remaining", "remainingString")
                .withConverter(Option::of)
                .withSize(-1)
                .build();
    }

    @Bean
    public static ArgumentConverter<Integer[]> remainingIntegersArgumentConverter() {
        return ArgumentConverterImpl.builder(Integer[].class, "remainingInt")
                .withConverter(in -> {
                    final String[] parts = in.split(" ");
                    final Integer[] integers = new Integer[parts.length];
                    for (int i = 0; i < parts.length; i++) {
                        final String part = parts[i];
                        integers[i] = BuiltInStringTypeAdapters.INTEGER.adapt(part).get();
                    }
                    return Option.of(integers);
                })
                .withSize(-1)
                .build();
    }
}
