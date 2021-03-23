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

package org.dockbox.selene.integrated;

import org.dockbox.selene.api.Players;
import org.dockbox.selene.api.Worlds;
import org.dockbox.selene.api.annotations.module.ArgumentProvider;
import org.dockbox.selene.api.annotations.module.Module;
import org.dockbox.selene.api.command.context.ArgumentConverter;
import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.common.ResourceService;
import org.dockbox.selene.api.i18n.entry.IntegratedResource;
import org.dockbox.selene.api.module.ModuleManager;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.location.position.Location;
import org.dockbox.selene.api.objects.location.dimensions.World;
import org.dockbox.selene.api.objects.player.Player;
import org.dockbox.selene.api.objects.tuple.Vector3N;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.server.config.GlobalConfig;
import org.dockbox.selene.api.server.properties.InjectableType;
import org.dockbox.selene.api.server.properties.InjectorProperty;
import org.dockbox.selene.api.text.Text;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.common.command.convert.impl.CommandValueConverter;
import org.jetbrains.annotations.NonNls;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings({ "unused", "ClassWithTooManyFields" })
@ArgumentProvider(module = IntegratedServer.class)
public final class DefaultArgumentConverters implements InjectableType {

    public static final ArgumentConverter<String> STRING = new CommandValueConverter<>(String.class, (Function<String, Exceptional<String>>) Exceptional::ofNullable, "string");

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
    }, in -> SeleneUtils.asList("true", "false", "yes", "no"), "bool", "boolean");

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
                            .orElse(Selene.provide(GlobalConfig.class).getDefaultLanguage());
        }
        return Exceptional.of(lang);
    }, in -> {
        List<String> suggestions = SeleneUtils.emptyList();
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
                        return new Vector3N(x, y, z);
                    }),
            "vec3", "vector", "v3n"
    );

    public static final ArgumentConverter<World> WORLD = new CommandValueConverter<>(World.class, in -> {
        Worlds wss = Selene.provide(Worlds.class);
        Exceptional<World> world = wss.getWorld(in);
        return world.orElseSupply(
                () -> {
                    UUID uuid = UUID.fromString(in);
                    return wss.getWorld(uuid).orNull();
                });
    }, "world");

    public static final ArgumentConverter<Location> LOCATION = new CommandValueConverter<>(Location.class, (cs, in) -> {
        String[] xyzw = in.split(",");
        String xyz = String.join(",", xyzw[0], xyzw[1], xyzw[2]);
        Vector3N vec = VECTOR.convert(cs, xyz).orElse(new Vector3N(0, 0, 0));
        World world = WORLD.convert(cs, xyzw[3]).orElse(World.empty());

        return Exceptional.of(new Location(vec, world));
    }, "location", "position", "pos");

    public static final ArgumentConverter<ResourceEntry> RESOURCE = new CommandValueConverter<>(ResourceEntry.class, in -> {
        ResourceService rs = Selene.provide(ResourceService.class);
        in = rs.createValidKey(in);

        Exceptional<? extends ResourceEntry> or = rs.getExternalResource(in);
        if (or.isPresent()) return or.map(ResourceEntry.class::cast);

        String finalValue = in;
        return Exceptional.of(() -> IntegratedResource.valueOf(finalValue));
    }, "resource", "i18n", "translation");

    public static final ArgumentConverter<Player> PLAYER = new CommandValueConverter<>(Player.class, in -> {
        Players pss = Selene.provide(Players.class);
        Exceptional<Player> player = pss.getPlayer(in);
        return player.orElseSupply(
                () -> {
                    try {
                        UUID uuid = UUID.fromString(in);
                        return pss.getPlayer(uuid).orNull();
                    }
                    catch (IllegalArgumentException e) {
                        //noinspection ReturnOfNull
                        return null;
                    }
                });
    }, in -> Selene.provide(Players.class).getOnlinePlayers().stream()
            .map(Player::getName)
            .filter(n -> n.startsWith(in))
            .collect(Collectors.toList()),
            "player", "user");

    public static final ArgumentConverter<Duration> DURATION = new CommandValueConverter<>(Duration.class, SeleneUtils::durationOf, "duration");

    public static final ArgumentConverter<Module> MODULE = new CommandValueConverter<>(Module.class, in -> Selene.provide(ModuleManager.class)
            .getHeader(in), in ->
            Selene.provide(ModuleManager.class).getRegisteredModuleIds().stream()
                    .filter(id -> id.toLowerCase().contains(in.toLowerCase()))
                    .collect(Collectors.toList()),
            "module");

    public static final ArgumentConverter<Text> TEXT = new CommandValueConverter<>(Text.class, in -> Exceptional.of(Text.of(in)), "text");

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) {
        Selene.log().info("Registered default command argument converters.");
    }
}
