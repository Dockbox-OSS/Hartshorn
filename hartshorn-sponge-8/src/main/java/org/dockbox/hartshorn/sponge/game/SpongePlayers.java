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

package org.dockbox.hartshorn.sponge.game;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.server.minecraft.players.Players;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.dockbox.hartshorn.sponge.util.SpongeUtil;
import org.spongepowered.api.Sponge;

import java.util.List;
import java.util.UUID;

public class SpongePlayers implements Players {

    @Override
    public List<Player> onlinePlayers() {
        return Sponge.server().onlinePlayers().stream()
                .map(SpongeConvert::fromSponge)
                .toList();
    }

    @Override
    public Exceptional<Player> player(String name) {
        return SpongeUtil.awaitOption(Sponge.server().userManager().load(name))
                .map(SpongeConvert::fromSponge);
    }

    @Override
    public Exceptional<Player> player(UUID uuid) {
        return SpongeUtil.await(Sponge.server().userManager().loadOrCreate(uuid))
                .map(SpongeConvert::fromSponge);
    }
}
