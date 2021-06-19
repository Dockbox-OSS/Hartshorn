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

package org.dockbox.hartshorn.sponge.util;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.server.minecraft.players.Players;
import org.dockbox.hartshorn.sponge.objects.targets.SpongePlayer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpongePlayers implements Players {
    @NotNull
    @Override
    public List<Player> getOnlinePlayers() {
        return Sponge.getServer().getOnlinePlayers().stream()
                .map(player -> new SpongePlayer(player.getUniqueId(), player.getName()))
                .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Exceptional<Player> getPlayer(@NotNull String name) {
        return SpongePlayers.getPlayer(Exceptional.of(Sponge.getServer().getPlayer(name)), name);
    }

    @NotNull
    @Override
    public Exceptional<Player> getPlayer(@NotNull UUID uuid) {
        return SpongePlayers.getPlayer(Exceptional.of(Sponge.getServer().getPlayer(uuid)), uuid);
    }

    private static Exceptional<Player> getPlayer(
            Exceptional<org.spongepowered.api.entity.living.player.Player> osp, Object obj) {
        if (osp.present()) {
            return osp.map(p -> new SpongePlayer(p.getUniqueId(), p.getName()));
        }
        else {
            Exceptional<Player> player = Exceptional.empty();
            Exceptional<UserStorageService> ouss = Exceptional.of(Sponge.getServiceManager().provide(UserStorageService.class));
            Exceptional<User> ou;
            if (obj instanceof UUID) {
                ou = ouss.orElse(uss -> Exceptional.of(uss.get((UUID) obj)));
            }
            else {
                try {
                    ou = ouss.orElse(uss -> Exceptional.of(uss.get(obj.toString())));
                }
                catch (IllegalArgumentException e) {
                    // Typically thrown if a username is invalid (length<0 or >=16)
                    // See org.spongepowered.common.service.user.SpongeUserStorageService.get:64
                    ou = Exceptional.of(e);
                }
            }
            if (ou.present()) player = ou.map(u -> new SpongePlayer(u.getUniqueId(), u.getName()));
            return player;
        }
    }
}
