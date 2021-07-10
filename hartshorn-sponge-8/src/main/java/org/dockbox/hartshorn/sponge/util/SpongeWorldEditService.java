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
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.worldedit.WorldEditService;
import org.dockbox.hartshorn.worldedit.region.Clipboard;
import org.dockbox.hartshorn.worldedit.region.Mask;
import org.dockbox.hartshorn.worldedit.region.Pattern;
import org.dockbox.hartshorn.worldedit.region.Region;

import java.util.Collection;

/**
 * Placeholder for WorldEdit service
 */
@Binds(WorldEditService.class)
public class SpongeWorldEditService implements WorldEditService {
    
    @Override
    public Exceptional<Region> getPlayerSelection(Player player) {
        return Exceptional.empty();
    }

    @Override
    public void setPlayerSelection(Player player, Region region) {
        // Nothing happens
    }

    @Override
    public Exceptional<Clipboard> getPlayerClipboard(Player player) {
        return Exceptional.empty();
    }

    @Override
    public void setPlayerClipboard(Player player, Clipboard clipboard) {
        // Nothing happens
    }

    @Override
    public void replace(Region region, Mask mask, Pattern pattern, Player cause) {
        // Nothing happens
    }

    @Override
    public void set(Region region, Pattern pattern, Player cause) {
        // Nothing happens
    }

    @Override
    public Exceptional<Pattern> parsePattern(String pattern, Player cause) {
        return Exceptional.empty();
    }

    @Override
    public Exceptional<Mask> parseMask(String mask, Player cause) {
        return Exceptional.empty();
    }

    @Override
    public void replace(Region region, Collection<Item> mask, Collection<Item> pattern, Player cause) {
        // Nothing happens
    }

    @Override
    public void set(Region region, Collection<Item> pattern, Player cause) {
        // Nothing happens
    }

    @Override
    public boolean hasActiveGmask(Player player) {
        return false;
    }

    @Override
    public Exceptional<Mask> getGmask(Player player) {
        return Exceptional.empty();
    }

    @Override
    public void setGmask(Player player, Mask mask) {
        // Nothing happens
    }
}
