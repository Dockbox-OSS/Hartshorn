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

package org.dockbox.selene.server.minecraft.service;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.i18n.permissions.Permission;
import org.dockbox.selene.api.i18n.text.Text;
import org.dockbox.selene.di.annotations.Binds;
import org.dockbox.selene.server.minecraft.players.Player;
import org.dockbox.selene.server.minecraft.players.Players;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

@Binds(BroadcastService.class)
public class SimpleBroadcastService implements BroadcastService {
    @Override
    public void broadcastPublic(@NotNull Text message) {
        Selene.context().get(Players.class).getOnlinePlayers().forEach(message::send);
    }

    @Override
    public void broadcastWithFilter(@NotNull Text message, @NotNull Predicate<Player> filter) {
        SimpleBroadcastService.sendWithPredicate(message, filter);
    }

    @Override
    public void broadcastForPermission(@NotNull Text message, @NotNull Permission permission) {
        SimpleBroadcastService.sendWithPredicate(message, p -> p.hasPermission(permission));
    }

    @Override
    public void broadcastForPermission(@NotNull Text message, @NotNull String permission) {
        SimpleBroadcastService.sendWithPredicate(message, p -> p.hasPermission(permission));
    }

    @Override
    public void broadcastForPermissionWithFilter(@NotNull Text message, @NotNull Permission permission, @NotNull Predicate<Player> filter) {
        SimpleBroadcastService.sendWithPredicate(message, p -> p.hasPermission(permission) && filter.test(p));
    }

    @Override
    public void broadcastForPermissionWithFilter(@NotNull Text message, @NotNull String permission, @NotNull Predicate<Player> filter) {
        SimpleBroadcastService.sendWithPredicate(message, p -> p.hasPermission(permission) && filter.test(p));
    }

    private static void sendWithPredicate(Text message, Predicate<Player> filter) {
        Selene.context().get(Players.class).getOnlinePlayers().stream()
                .filter(filter)
                .forEach(message::send);
    }
}
