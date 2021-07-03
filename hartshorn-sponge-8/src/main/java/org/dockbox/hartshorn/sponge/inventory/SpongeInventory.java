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

package org.dockbox.hartshorn.sponge.inventory;

import org.dockbox.hartshorn.api.CheckedFunction;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.Slot;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface SpongeInventory {

    CheckedFunction<Slot, Item> SLOT_LOOKUP = slot -> SpongeConvert.fromSponge(slot.peek());
    Function<Slot, Item> SLOT_LOOKUP_FN = slot -> SpongeConvert.fromSponge(slot.peek());

    default Collection<Item> getAllItems(Inventory inventory) {
        return inventory.slots().stream()
                .map(SLOT_LOOKUP_FN)
                .collect(Collectors.toList());
    }

}
