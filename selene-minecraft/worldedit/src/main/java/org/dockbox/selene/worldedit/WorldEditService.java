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

package org.dockbox.selene.worldedit;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.di.annotations.Required;
import org.dockbox.selene.server.minecraft.item.Item;
import org.dockbox.selene.server.minecraft.players.Player;
import org.dockbox.selene.worldedit.region.Clipboard;
import org.dockbox.selene.worldedit.region.Mask;
import org.dockbox.selene.worldedit.region.Pattern;
import org.dockbox.selene.worldedit.region.Region;

import java.util.Collection;

@Required
public interface WorldEditService {

    Exceptional<Region> getPlayerSelection(Player player);

    void setPlayerSelection(Player player, Region region);

    Exceptional<Clipboard> getPlayerClipboard(Player player);

    void setPlayerClipboard(Player player, Clipboard clipboard);

    void replace(Region region, Mask mask, Pattern pattern, Player cause);

    void set(Region region, Pattern pattern, Player cause);

    Exceptional<Pattern> parsePattern(String pattern, Player cause);

    Exceptional<Mask> parseMask(String mask, Player cause);

    void replace(Region region, Collection<Item> mask, Collection<Item> pattern, Player cause);

    void set(Region region, Collection<Item> pattern, Player cause);

    boolean hasActiveGmask(Player player);

    Exceptional<Mask> getGmask(Player player);

    void setGmask(Player player, Mask mask);
}
