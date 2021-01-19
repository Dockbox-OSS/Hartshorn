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

package org.dockbox.selene.core.external;

import org.dockbox.selene.core.external.pattern.Mask;
import org.dockbox.selene.core.external.pattern.Pattern;
import org.dockbox.selene.core.external.region.Clipboard;
import org.dockbox.selene.core.external.region.Region;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.player.Player;

import java.util.Collection;

public interface WorldEditService {

    Exceptional<Region> getPlayerSelection(Player player);

    void setPlayerSelection(Player player, Region region);

    Exceptional<Clipboard> getPlayerClipboard(Player player);

    void setPlayerClipboard(Player player, Clipboard clipboard);

    void replace(Region region, Mask mask, Pattern pattern, Player cause);

    void set(Region region, Pattern pattern, Player cause);

    Exceptional<Pattern> parsePattern(String pattern);

    Exceptional<Mask> parseMask(String mask);

    void replace(Region region, Collection<Item> mask, Collection<Item> pattern, Player cause);

    void set(Region region, Collection<Item> pattern, Player cause);
}
