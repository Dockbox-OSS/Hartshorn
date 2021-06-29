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
import org.dockbox.hartshorn.api.i18n.ResourceService;
import org.dockbox.hartshorn.api.i18n.common.Language;
import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.di.annotations.Service;
import org.dockbox.hartshorn.di.properties.InjectableType;
import org.dockbox.hartshorn.di.properties.InjectorProperty;
import org.dockbox.hartshorn.di.services.ServiceContainer;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.NonNls;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings({ "unused", "ClassWithTooManyFields" })
@Service
public final class DefaultArgumentConverters implements InjectableType {

    public static final ArgumentConverter<String> STRING = CommandValueConverter.builder(String.class, "string")
            .withConverter((Function<String, Exceptional<String>>) Exceptional::of)
            .build();

    public static final ArgumentConverter<Character> CHARACTER = CommandValueConverter.builder(Character.class, "char", "character")
            .withConverter(in -> {
                int length = in.length();
                return 1 == length ? Exceptional.of(in.charAt(0)) : Exceptional.empty();
            }).build();

    public static final ArgumentConverter<Boolean> BOOLEAN = CommandValueConverter.builder(Boolean.class, "bool", "boolean")
            .withConverter(in -> switch (in) {
                case "yes" -> Exceptional.of(true);
                case "no" -> Exceptional.of(false);
                default -> Exceptional.of(in).map(Boolean::parseBoolean);
            }).withSuggestionProvider(in -> HartshornUtils.asList("true", "false", "yes", "no"))
            .build();

    public static final ArgumentConverter<Double> DOUBLE = CommandValueConverter.builder(Double.class, "double")
            .withConverter(in -> Exceptional.of(in).map(Double::parseDouble))
            .build();

    public static final ArgumentConverter<Float> FLOAT = CommandValueConverter.builder(Float.class, "float")
            .withConverter(in -> Exceptional.of(in).map(Float::parseFloat))
            .build();

    public static final ArgumentConverter<Integer> INTEGER = CommandValueConverter.builder(Integer.class, "int", "integer")
            .withConverter(in -> Exceptional.of(in).map(Integer::parseInt))
            .build();

    public static final ArgumentConverter<Long> LONG = CommandValueConverter.builder(Long.class, "long")
            .withConverter(in -> Exceptional.of(in).map(Long::parseLong))
            .build();

    public static final ArgumentConverter<Short> SHORT = CommandValueConverter.builder(Short.class, "short")
            .withConverter(in -> Exceptional.of(in).map(Short::parseShort))
            .build();

    public static final ArgumentConverter<Language> LANGUAGE = CommandValueConverter.builder(Language.class, "lang", "language")
            .withConverter((@NonNls String in) -> {
                Language lang;
                try {
                    lang = Language.valueOf(in);
                }
                catch (NullPointerException | IllegalArgumentException e) {
                    lang =
                            Arrays.stream(Language.values())
                                    .filter(l -> l.getNameEnglish().equals(in) || l.getNameLocalized().equals(in))
                                    .findFirst()
                                    .orElse(Language.EN_US);
                }
                return Exceptional.of(lang);
            }).withSuggestionProvider(in -> {
                List<String> suggestions = HartshornUtils.emptyList();
                for (Language lang : Language.values()) {
                    suggestions.add(lang.getCode());
                    suggestions.add(lang.getNameEnglish());
                    suggestions.add(lang.getNameLocalized());
                }
                return suggestions.stream()
                        .filter(lang -> lang.toLowerCase().contains(in.toLowerCase()))
                        .collect(Collectors.toList());
            }).build();

    public static final ArgumentConverter<UUID> UNIQUE_ID = CommandValueConverter.builder(UUID.class, "uuid", "uniqueId")
            .withConverter(in -> Exceptional.of(in).map(UUID::fromString))
            .build();

    public static final ArgumentConverter<Vector3N> VECTOR = CommandValueConverter.builder(Vector3N.class, "vec3", "vector", "v3n")
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

    public static final ArgumentConverter<Duration> DURATION = CommandValueConverter.builder(Duration.class, "duration")
            .withConverter(HartshornUtils::durationOf)
            .build();

    public static final ArgumentConverter<ResourceEntry> RESOURCE = CommandValueConverter.builder(ResourceEntry.class, "resource", "i18n", "translation")
            .withConverter(in -> {
                ResourceService rs = Hartshorn.context().get(ResourceService.class);
                String validKey = rs.createValidKey(in);

                Exceptional<? extends ResourceEntry> or = rs.get(validKey);
                if (or.present()) return or.map(ResourceEntry.class::cast);

                return Hartshorn.context().get(ResourceService.class).get(validKey);
            }).build();

    public static final ArgumentConverter<Text> TEXT = CommandValueConverter.builder(Text.class, "text")
            .withConverter(in -> Exceptional.of(Text.of(in)))
            .build();

    public static final ArgumentConverter<ServiceContainer> SERVICE = CommandValueConverter.builder(ServiceContainer.class, "service")
            .withConverter(in -> Exceptional.of(Hartshorn.context()
                    .locator().containers().stream()
                    .filter(container -> container.getId().equalsIgnoreCase(in))
                    .findFirst()))
            .withSuggestionProvider((in) -> Hartshorn.context()
                    .locator().containers().stream()
                    .map(ServiceContainer::getId)
                    .filter(id -> id.toLowerCase(Locale.ROOT).startsWith(in.toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList()))
            .build();

    public static final ArgumentConverter<String> REMAINING_STRING = CommandValueConverter.builder(String.class, "remaining", "remainingString")
            .withConverter((Function<String, Exceptional<String>>) Exceptional::of)
            .withSize(-1)
            .build();

    public static final ArgumentConverter<Integer[]> REMAINING_INTS = CommandValueConverter.builder(Integer[].class, "remainingInt")
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

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) {
        Hartshorn.log().info("Registered default command argument converters.");
    }
}
