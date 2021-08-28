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

package org.dockbox.hartshorn.test.objects.inventory;

import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.server.minecraft.entities.ArmorStandInventory;
import org.dockbox.hartshorn.server.minecraft.inventory.Slot;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.ItemTypes;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Collection;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JUnitArmorStandInventory implements ArmorStandInventory {

    private final Map<Slot, Item> items = HartshornUtils.emptyMap();
    @Getter private final ApplicationContext applicationContext;

    @Override
    public Collection<Item> items() {
        return HartshornUtils.asUnmodifiableList(this.items.values());
    }

    @Override
    public boolean give(final Item item) {
        for (final Slot slot : Slot.values()) {
            if (!this.items.containsKey(slot)) {
                this.slot(item, slot);
                return true;
            }
        }
        return false;
    }

    @Override
    public Item slot(final Slot slot) {
        return this.items.getOrDefault(slot, Item.of(this.applicationContext(), ItemTypes.AIR));
    }

    @Override
    public void slot(final Item item, final Slot slot) {
        this.items.put(slot, item);
    }
}
