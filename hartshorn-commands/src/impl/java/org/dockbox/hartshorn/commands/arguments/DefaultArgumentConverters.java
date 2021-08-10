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

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.di.services.ComponentContainer;
import org.dockbox.hartshorn.i18n.ResourceService;
import org.dockbox.hartshorn.i18n.common.Language;
import org.dockbox.hartshorn.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.NonNls;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;

@SuppressWarnings({ "unused", "ClassWithTooManyFields" })
@Service
public final class DefaultArgumentConverters {

    public static final ArgumentConverter<String> STRING = ArgumentConverterImpl.builder(String.class, "string")
            .withConverter((Function<String, Exceptional<String>>) Exceptional::of)
            .build();

    public static final ArgumentConverter<Character> CHARACTER = ArgumentConverterImpl.builder(Character.class, "char", "character")
            .withConverter(in -> {
                int length = in.length();
                return 1 == length ? Exceptional.of(in.charAt(0)) : Exceptional.empty();
            }).build();

    public static final ArgumentConverter<Boolean> BOOLEAN = ArgumentConverterImpl.builder(Boolean.class, "bool", "boolean")
            .withConverter(in -> switch (in) {
                case "yes" -> Exceptional.of(true);
                case "no" -> Exceptional.of(false);
                default -> Exceptional.of(in).map(Boolean::parseBoolean);
            }).withSuggestionProvider(in -> HartshornUtils.asList("true", "false", "yes", "no"))
            .build();

    public static final ArgumentConverter<Double> DOUBLE = ArgumentConverterImpl.builder(Double.class, "double")
            .withConverter(in -> Exceptional.of(in).map(Double::parseDouble))
            .build();

    public static final ArgumentConverter<Float> FLOAT = ArgumentConverterImpl.builder(Float.class, "float")
            .withConverter(in -> Exceptional.of(in).map(Float::parseFloat))
            .build();

    public static final ArgumentConverter<Integer> INTEGER = ArgumentConverterImpl.builder(Integer.class, "int", "integer")
            .withConverter(in -> Exceptional.of(in).map(Integer::parseInt))
            .build();

    public static final ArgumentConverter<Long> LONG = ArgumentConverterImpl.builder(Long.class, "long")
            .withConverter(in -> Exceptional.of(in).map(Long::parseLong))
            .build();

    public static final ArgumentConverter<Short> SHORT = ArgumentConverterImpl.builder(Short.class, "short")
            .withConverter(in -> Exceptional.of(in).map(Short::parseShort))
            .build();

    public static final ArgumentConverter<Language> LANGUAGE = ArgumentConverterImpl.builder(Language.class, "lang", "language")
            .withConverter((@NonNls String in) -> {
                Language lang;
                try {
                    lang = Language.valueOf(in);
                }
                catch (NullPointerException | IllegalArgumentException e) {
                    lang =
                            Arrays.stream(Language.values())
                                    .filter(l -> l.nameEnglish().equals(in) || l.nameLocalized().equals(in))
                                    .findFirst()
                                    .orElse(Language.EN_US);
                }
                return Exceptional.of(lang);
            }).withSuggestionProvider(in -> {
                List<String> suggestions = HartshornUtils.emptyList();
                for (Language lang : Language.values()) {
                    suggestions.add(lang.code());
                    suggestions.add(lang.nameEnglish());
                    suggestions.add(lang.nameLocalized());
                }
                return suggestions.stream()
                        .filter(lang -> lang.toLowerCase().contains(in.toLowerCase()))
                        .toList();
            }).build();

    public static final ArgumentConverter<UUID> UNIQUE_ID = ArgumentConverterImpl.builder(UUID.class, "uuid", "uniqueId")
            .withConverter(in -> Exceptional.of(in).map(UUID::fromString))
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

    public static final ArgumentConverter<ResourceEntry> RESOURCE = ArgumentConverterImpl.builder(ResourceEntry.class, "resource", "i18n", "translation")
            .withConverter(in -> {
                ResourceService rs = Hartshorn.context().get(ResourceService.class);
                String validKey = rs.createValidKey(in);

                Exceptional<? extends ResourceEntry> or = rs.get(validKey);
                if (or.present()) return or.map(ResourceEntry.class::cast);

                return Hartshorn.context().get(ResourceService.class).get(validKey);
            }).build();

    public static final ArgumentConverter<Text> TEXT = ArgumentConverterImpl.builder(Text.class, "text")
            .withConverter(in -> Exceptional.of(Text.of(in)))
            .build();

    public static final ArgumentConverter<ComponentContainer> SERVICE = ArgumentConverterImpl.builder(ComponentContainer.class, "service")
            .withConverter(in -> Exceptional.of(Hartshorn.context()
                    .locator().containers().stream()
                    .filter(container -> container.id().equalsIgnoreCase(in))
                    .findFirst()))
            .withSuggestionProvider((in) -> Hartshorn.context()
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
}
