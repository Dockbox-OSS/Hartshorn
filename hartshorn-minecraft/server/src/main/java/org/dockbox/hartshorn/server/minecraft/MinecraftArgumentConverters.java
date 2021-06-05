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

package org.dockbox.hartshorn.server.minecraft;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.commands.context.ArgumentConverter;
import org.dockbox.hartshorn.commands.convert.CommandValueConverter;
import org.dockbox.hartshorn.commands.convert.DefaultArgumentConverters;
import org.dockbox.hartshorn.di.annotations.Service;
import org.dockbox.hartshorn.di.properties.InjectableType;
import org.dockbox.hartshorn.di.properties.InjectorProperty;
import org.dockbox.hartshorn.server.minecraft.dimension.Worlds;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.server.minecraft.players.Players;

import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings({ "unused", "ClassWithTooManyFields" })
@Service
public final class MinecraftArgumentConverters implements InjectableType {

    public static final ArgumentConverter<World> WORLD = new CommandValueConverter<>(World.class, in -> {
        Worlds wss = Hartshorn.context().get(Worlds.class);
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

    public static final ArgumentConverter<Player> PLAYER = new CommandValueConverter<>(Player.class, in -> {
        Players pss = Hartshorn.context().get(Players.class);
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
    }, in -> Hartshorn.context().get(Players.class).getOnlinePlayers().stream()
            .map(Player::getName)
            .filter(n -> n.startsWith(in))
            .collect(Collectors.toList()),
            "player", "user");

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) {
        Hartshorn.log().info("Registered Minecraft specific command argument converters.");
    }
}