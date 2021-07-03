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
import org.dockbox.hartshorn.server.minecraft.inventory.Slot;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.inventory.InventoryRow;
import org.dockbox.hartshorn.server.minecraft.players.inventory.PlayerInventory;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Collection;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SpongePlayerInventory extends PlayerInventory {

    private final SpongePlayer player;

    @Override
    public void setSlot(Item item, int index) {

    }

    @Override
    public Item getSlot(int index) {
        return null;
    }

    @Override
    public Collection<Item> getAllItems() {
        return null;
    }

    @Override
    public boolean give(Item item) {
        return false;
    }

    @Override
    public Item getSlot(int row, int column) {
        return null;
    }

    @Override
    public void setSlot(Item item, int row, int column) {

    }

    @Override
    public Item getSlot(Slot slot) {
        return null;
    }

    @Override
    public void setSlot(Item item, Slot slot) {

    }

    @Override
    public Exceptional<InventoryRow> getRow(int index) {
        return null;
    }

    private Exceptional<org.spongepowered.api.item.inventory.entity.PlayerInventory> player() {
        return this.player.player().map(Player::inventory);
    }
}
