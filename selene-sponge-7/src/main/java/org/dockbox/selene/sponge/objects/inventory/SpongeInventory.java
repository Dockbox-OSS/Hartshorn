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

package org.dockbox.selene.sponge.objects.inventory;

import org.dockbox.selene.server.minecraft.item.Item;
import org.dockbox.selene.server.minecraft.players.inventory.AbstractInventoryRow;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.Slot;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface SpongeInventory {

    Function<Slot, Item> SLOT_LOOKUP = slot -> slot.peek()
            .map(SpongeConversionUtil::fromSponge)
            .map(referencedItem -> (Item) referencedItem)
            .orElseGet(AbstractInventoryRow.AIR);

    default Collection<Item> getAllItems(Inventory inventory) {
        return StreamSupport.stream(inventory.slots().spliterator(), false)
                .map(slot -> (org.spongepowered.api.item.inventory.Slot) slot)
                .map(SLOT_LOOKUP)
                .collect(Collectors.toList());
    }

}
