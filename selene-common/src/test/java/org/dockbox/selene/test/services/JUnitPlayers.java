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

package org.dockbox.selene.test.services;

import org.dockbox.selene.api.Players;
import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.player.Player;

import java.util.List;
import java.util.UUID;

public class JUnitPlayers implements Players {

    @Override
    public List<Player> getOnlinePlayers() {
        return null;
    }

    @Override
    public Exceptional<Player> getPlayer(String name) {
        return null;
    }

    @Override
    public Exceptional<Player> getPlayer(UUID uuid) {
        return null;
    }

    @Override
    public void setLanguagePreference(UUID uuid, Language language) {

    }

    @Override
    public Language getLanguagePreference(UUID uuid) {
        return null;
    }
}
