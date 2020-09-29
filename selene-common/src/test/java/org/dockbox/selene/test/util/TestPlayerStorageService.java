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
import org.dockbox.selene.core.objects.user.Player;
import org.dockbox.selene.core.util.player.PlayerStorageService;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TestPlayerStorageService implements PlayerStorageService {
    @NotNull
    @Override
    public List<Player> getOnlinePlayers() {
        return null;
    }

    @NotNull
    @Override
    public Optional<Player> getPlayer(@NotNull String name) {
        return Optional.empty();
    }

    @NotNull
    @Override
    public Optional<Player> getPlayer(@NotNull UUID uuid) {
        return Optional.empty();
    }

    @Override
    public void setLanguagePreference(@NotNull UUID uuid, @NotNull Language lang) {

    }

    @NotNull
    @Override
    public Language getLanguagePreference(@NotNull UUID uuid) {
        return null;
    }
}
