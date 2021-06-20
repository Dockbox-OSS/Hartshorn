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

package org.dockbox.hartshorn.commands.convert;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.api.i18n.ResourceService;
import org.dockbox.hartshorn.api.i18n.common.Language;
import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.commands.context.ArgumentConverter;
import org.dockbox.hartshorn.di.annotations.Service;
import org.dockbox.hartshorn.di.properties.InjectableType;
import org.dockbox.hartshorn.di.properties.InjectorProperty;
import org.dockbox.hartshorn.di.services.ServiceContainer;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.NonNls;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings({ "unused", "ClassWithTooManyFields" })
@Service
public final class DefaultArgumentConverters implements InjectableType {

    public static final ArgumentConverter<String> STRING = new CommandValueConverter<>(String.class, (Function<String, Exceptional<String>>) Exceptional::of, "string");

    public static final ArgumentConverter<Character> CHARACTER = new CommandValueConverter<>(Character.class, in -> {
        int length = in.length();
        return 1 == length ? Exceptional.of(in.charAt(0)) : Exceptional.empty();
    },
            "char", "character"
    );

    public static final ArgumentConverter<Boolean> BOOLEAN = new CommandValueConverter<>(Boolean.class, in -> {
        switch (in) {
            case "yes":
                return Exceptional.of(true);
            case "no":
                return Exceptional.of(false);
            default:
                return Exceptional.of(in).map(Boolean::parseBoolean);
        }
    }, in -> HartshornUtils.asList("true", "false", "yes", "no"), "bool", "boolean");

    public static final ArgumentConverter<Double> DOUBLE = new CommandValueConverter<>(Double.class, in -> Exceptional.of(in)
            .map(Double::parseDouble), "double");

    public static final ArgumentConverter<Float> FLOAT = new CommandValueConverter<>(Float.class, in -> Exceptional.of(in)
            .map(Float::parseFloat), "float");

    public static final ArgumentConverter<Integer> INTEGER = new CommandValueConverter<>(Integer.class, in -> Exceptional.of(in)
            .map(Integer::parseInt), "int", "integer");

    public static final ArgumentConverter<Long> LONG = new CommandValueConverter<>(Long.class, in -> Exceptional.of(in)
            .map(Long::parseLong), "long");

    public static final ArgumentConverter<Short> SHORT = new CommandValueConverter<>(Short.class, in -> Exceptional.of(in)
            .map(Short::parseShort), "short");

    public static final ArgumentConverter<Language> LANGUAGE = new CommandValueConverter<>(Language.class, (@NonNls String in) -> {
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
    }, in -> {
        List<String> suggestions = HartshornUtils.emptyList();
        for (Language lang : Language.values()) {
            suggestions.add(lang.getCode());
            suggestions.add(lang.getNameEnglish());
            suggestions.add(lang.getNameLocalized());
        }
        return suggestions.stream()
                .filter(lang -> lang.toLowerCase().contains(in.toLowerCase()))
                .collect(Collectors.toList());
    }, "lang", "language"
    );

    public static final ArgumentConverter<UUID> UNIQUE_ID = new CommandValueConverter<>(UUID.class, in -> Exceptional
            .of(() -> UUID.fromString(in)), "uuid", "uniqueid");

    public static final ArgumentConverter<Vector3N> VECTOR = new CommandValueConverter<>(Vector3N.class, in ->
            Exceptional.of(
                    () -> {
                        String[] xyz = in.split(",");
                        // IndexOutOfBounds is caught by Callable handle in Exceptional
                        double x = Double.parseDouble(xyz[0]);
                        double y = Double.parseDouble(xyz[1]);
                        double z = Double.parseDouble(xyz[2]);
                        return Vector3N.of(x, y, z);
                    }),
            "vec3", "vector", "v3n"
    );

    public static final ArgumentConverter<Duration> DURATION = new CommandValueConverter<>(Duration.class, HartshornUtils::durationOf, "duration");

    public static final ArgumentConverter<ResourceEntry> RESOURCE = new CommandValueConverter<>(ResourceEntry.class, in -> {
        ResourceService rs = Hartshorn.context().get(ResourceService.class);
        String validKey = rs.createValidKey(in);

        Exceptional<? extends ResourceEntry> or = rs.get(validKey);
        if (or.present()) return or.map(ResourceEntry.class::cast);

        return Hartshorn.context().get(ResourceService.class).get(validKey);
    }, "resource", "i18n", "translation");

    public static final ArgumentConverter<Text> TEXT = new CommandValueConverter<>(Text.class, in -> Exceptional.of(Text.of(in)), "text");

    public static final ArgumentConverter<ServiceContainer> SERVICE = new CommandValueConverter<ServiceContainer>(ServiceContainer.class, in -> Exceptional.of(Hartshorn.context()
            .locator().containers().stream()
            .filter(container -> container.getId().equals(in))
            .findFirst()
    ), "service");

    public static final ArgumentConverter<String> REMAINING_STRING = new CommandValueConverter<>(String.class, Exceptional::of, -1, "remaining", "remainingString");

    public static final ArgumentConverter<Integer[]> REMAINING_INTS = new CommandValueConverter<>(Integer[].class, in -> {
        String[] parts = in.split(" ");
        Integer[] integers = new Integer[parts.length];
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            integers[i] = INTEGER.convert(null, parts[i]).get();
        }
        return Exceptional.of(integers);
    }, -1, "remainingInt");

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) {
        Hartshorn.log().info("Registered default command argument converters.");
    }
}
