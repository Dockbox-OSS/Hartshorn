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

package org.dockbox.hartshorn.test.services;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.server.minecraft.players.Players;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class JUnitPlayers implements Players {

    private final Set<Player> players = HartshornUtils.emptyConcurrentSet();

    @Override
    public List<Player> onlinePlayers() {
        return HartshornUtils.asUnmodifiableList(this.players.stream().filter(Player::online).toList());
    }

    @Override
    public Exceptional<Player> player(final String name) {
        for (final Player player : this.players) if (player.name().equalsIgnoreCase(name)) return Exceptional.of(player);
        return Exceptional.empty();
    }

    @Override
    public Exceptional<Player> player(final UUID uuid) {
        for (final Player player : this.players) if (player.uniqueId().equals(uuid)) return Exceptional.of(player);
        return Exceptional.empty();
    }
}
