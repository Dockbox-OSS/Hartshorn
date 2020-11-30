/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.test.util;

import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.objects.user.Player;
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.core.util.player.PlayerStorageService;
import org.dockbox.selene.test.object.TestPlayer;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TestPlayerStorageService implements PlayerStorageService {

    private final Collection<Player> knownPlayers = SeleneUtils.emptyConcurrentList();

    public void registerPlayer(Player player) {
        this.knownPlayers.add(player);
    }

    @NotNull
    @Override
    public List<Player> getOnlinePlayers() {
        return this.knownPlayers.stream().filter(Player::isOnline).collect(Collectors.toList());
    }

    public void setOnline(TestPlayer player) {
        player.setOnline(true);
        this.knownPlayers.add(player);
    }

    public void setOffline(TestPlayer player) {
        player.setOnline(false);
        this.knownPlayers.add(player);
    }

    @NotNull
    @Override
    public Exceptional<Player> getPlayer(@NonNls @NotNull String name) {
        return Exceptional.of(this.knownPlayers.stream().filter(p -> p.getName().equals(name)).findFirst());
    }

    @NotNull
    @Override
    public Exceptional<Player> getPlayer(@NotNull UUID uuid) {
        return Exceptional.of(this.knownPlayers.stream().filter(p -> p.getUniqueId().equals(uuid)).findFirst());
    }

    @Override
    public void setLanguagePreference(@NotNull UUID uuid, @NotNull Language lang) {
        this.getPlayer(uuid).ifPresent(player -> {
            player.setLanguage(lang);
            this.knownPlayers.add(player);
        });
    }

    @NotNull
    @Override
    public Language getLanguagePreference(@NotNull UUID uuid) {
        return this.getPlayer(uuid).map(Player::getLanguage).orElse(Language.EN_US);
    }
}
