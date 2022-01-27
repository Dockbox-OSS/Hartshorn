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

import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.adapter.BuiltInStringTypeAdapters;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.domain.tuple.Vector3N;
import org.dockbox.hartshorn.core.services.ComponentContainer;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.i18n.TranslationService;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;

@Service(activators = UseCommands.class, permitProxying = false)
public final class DefaultArgumentConverters {

    public static final ArgumentConverter<String> STRING = ArgumentConverterImpl.builder(String.class, "string")
            .withConverter(BuiltInStringTypeAdapters.STRING)
            .build();

    public static final ArgumentConverter<Character> CHARACTER = ArgumentConverterImpl.builder(Character.class, "char", "character")
            .withConverter(BuiltInStringTypeAdapters.CHARACTER)
            .build();

    public static final ArgumentConverter<Boolean> BOOLEAN = ArgumentConverterImpl.builder(Boolean.class, "bool", "boolean")
            .withConverter(BuiltInStringTypeAdapters.BOOLEAN)
            .withSuggestionProvider(in -> List.of("true", "false", "yes", "no"))
            .build();

    public static final ArgumentConverter<Double> DOUBLE = ArgumentConverterImpl.builder(Double.class, "double")
            .withConverter(BuiltInStringTypeAdapters.DOUBLE)
            .build();

    public static final ArgumentConverter<Float> FLOAT = ArgumentConverterImpl.builder(Float.class, "float")
            .withConverter(BuiltInStringTypeAdapters.FLOAT)
            .build();

    public static final ArgumentConverter<Integer> INTEGER = ArgumentConverterImpl.builder(Integer.class, "int", "integer")
            .withConverter(BuiltInStringTypeAdapters.INTEGER)
            .build();

    public static final ArgumentConverter<Long> LONG = ArgumentConverterImpl.builder(Long.class, "long")
            .withConverter(BuiltInStringTypeAdapters.LONG)
            .build();

    public static final ArgumentConverter<Short> SHORT = ArgumentConverterImpl.builder(Short.class, "short")
            .withConverter(BuiltInStringTypeAdapters.SHORT)
            .build();

    public static final ArgumentConverter<UUID> UNIQUE_ID = ArgumentConverterImpl.builder(UUID.class, "uuid", "uniqueId")
            .withConverter(BuiltInStringTypeAdapters.UNIQUE_ID)
            .build();

    public static final ArgumentConverter<Vector3N> VECTOR = ArgumentConverterImpl.builder(Vector3N.class, "vec3", "vector", "v3n")
            .withConverter(in -> Exceptional.of(
                    () -> {
                        String[] xyz = in.split(",");
                        // IndexOutOfBounds is caught by Callable handle in Exceptional
                        double x = Double.parseDouble(xyz[0]);
                        double y = Double.parseDouble(xyz[1]);
                        double z = Double.parseDouble(xyz[2]);
                        return Vector3N.of(x, y, z);
                    }))
            .build();

    public static final ArgumentConverter<Duration> DURATION = ArgumentConverterImpl.builder(Duration.class, "duration")
            .withConverter(HartshornUtils::durationOf)
            .build();

    public static final ArgumentConverter<Message> MESSAGE = ArgumentConverterImpl.builder(Message.class, "resource", "i18n", "translation")
            .withConverter((src, in) -> {
                TranslationService rs = src.applicationContext().get(TranslationService.class);
                return rs.get(in);
            }).build();

    public static final ArgumentConverter<ComponentContainer> SERVICE = ArgumentConverterImpl.builder(ComponentContainer.class, "service")
            .withConverter((src, in) -> Exceptional.of(src.applicationContext()
                    .locator().containers().stream()
                    .filter(container -> container.id().equalsIgnoreCase(in))
                    .findFirst()))
            .withSuggestionProvider((src, in) -> src.applicationContext()
                    .locator().containers().stream()
                    .map(ComponentContainer::id)
                    .filter(id -> id.toLowerCase(Locale.ROOT).startsWith(in.toLowerCase(Locale.ROOT)))
                    .toList())
            .build();

    public static final ArgumentConverter<String> REMAINING_STRING = ArgumentConverterImpl.builder(String.class, "remaining", "remainingString")
            .withConverter((Function<String, Exceptional<String>>) Exceptional::of)
            .withSize(-1)
            .build();

    public static final ArgumentConverter<Integer[]> REMAINING_INTS = ArgumentConverterImpl.builder(Integer[].class, "remainingInt")
            .withConverter(in -> {
                String[] parts = in.split(" ");
                Integer[] integers = new Integer[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    String part = parts[i];
                    integers[i] = INTEGER.convert(null, parts[i]).get();
                }
                return Exceptional.of(integers);
            })
            .withSize(-1)
            .build();

    private DefaultArgumentConverters() {}
}
