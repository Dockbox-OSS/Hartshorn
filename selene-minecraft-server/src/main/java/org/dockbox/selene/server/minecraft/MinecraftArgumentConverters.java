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

package org.dockbox.selene.commands.convert;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.domain.tuple.Vector3N;
import org.dockbox.selene.api.i18n.ResourceService;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.api.i18n.entry.DefaultResource;
import org.dockbox.selene.api.i18n.text.Text;
import org.dockbox.selene.api.module.ModuleContainer;
import org.dockbox.selene.api.module.ModuleManager;
import org.dockbox.selene.commands.annotations.ArgumentProvider;
import org.dockbox.selene.commands.context.ArgumentConverter;
import org.dockbox.selene.di.properties.InjectableType;
import org.dockbox.selene.di.properties.InjectorProperty;
import org.dockbox.selene.minecraft.dimension.Worlds;
import org.dockbox.selene.minecraft.dimension.position.Location;
import org.dockbox.selene.minecraft.dimension.world.World;
import org.dockbox.selene.minecraft.players.Player;
import org.dockbox.selene.minecraft.players.Players;
import org.dockbox.selene.util.SeleneUtils;

import java.time.Duration;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings({ "unused", "ClassWithTooManyFields" })
@ArgumentProvider(module = Selene.class)
public final class MinecraftArgumentConverters implements InjectableType {

    public static final ArgumentConverter<World> WORLD = new CommandValueConverter<>(World.class, in -> {
        Worlds wss = Selene.provide(Worlds.class);
        Exceptional<World> world = wss.getWorld(in);
        return world.then(
                () -> {
                    UUID uuid = UUID.fromString(in);
                    return wss.getWorld(uuid).orNull();
                });
    }, "world");

    public static final ArgumentConverter<Location> LOCATION = new CommandValueConverter<>(Location.class, (cs, in) -> {
        String[] xyzw = in.split(",");
        String xyz = String.join(",", xyzw[0], xyzw[1], xyzw[2]);
        Vector3N vec = DefaultArgumentConverters.VECTOR.convert(cs, xyz).or(Vector3N.of(0, 0, 0));
        World world = WORLD.convert(cs, xyzw[3]).or(World.empty());

        return Exceptional.of(new Location(vec, world));
    }, "location", "position", "pos");

    public static final ArgumentConverter<ResourceEntry> RESOURCE = new CommandValueConverter<>(ResourceEntry.class, in -> {
        ResourceService rs = Selene.provide(ResourceService.class);
        in = rs.createValidKey(in);

        Exceptional<? extends ResourceEntry> or = rs.getExternalResource(in);
        if (or.present()) return or.map(ResourceEntry.class::cast);

        String finalValue = in;
        return Exceptional.of(() -> DefaultResource.valueOf(finalValue));
    }, "resource", "i18n", "translation");

    public static final ArgumentConverter<Player> PLAYER = new CommandValueConverter<>(Player.class, in -> {
        Players pss = Selene.provide(Players.class);
        Exceptional<Player> player = pss.getPlayer(in);
        return player.then(() -> {
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

    public static final ArgumentConverter<ModuleContainer> MODULE = new CommandValueConverter<>(ModuleContainer.class, in -> Selene.provide(ModuleManager.class)
            .getContainer(in), in ->
            Selene.provide(ModuleManager.class).getRegisteredModuleIds().stream()
                    .filter(id -> id.toLowerCase().contains(in.toLowerCase()))
                    .collect(Collectors.toList()),
            "module");

    public static final ArgumentConverter<Text> TEXT = new CommandValueConverter<>(Text.class, in -> Exceptional.of(Text.of(in)), "text");

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) {
        Selene.log().info("Registered Minecraft specific command argument converters.");
    }
}
