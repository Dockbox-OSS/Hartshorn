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

package org.dockbox.hartshorn.sponge.game.entity;

import org.dockbox.hartshorn.server.minecraft.entities.ArmorStandInventory;
import org.dockbox.hartshorn.server.minecraft.inventory.Slot;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.ItemTypes;
import org.dockbox.hartshorn.sponge.SpongeContextCarrier;
import org.dockbox.hartshorn.sponge.inventory.SpongeInventory;
import org.dockbox.hartshorn.sponge.util.SpongeAdapter;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;

import java.util.Collection;
import java.util.List;

public class SpongeArmorStandInventory implements SpongeInventory, ArmorStandInventory, SpongeContextCarrier {

    private final SpongeArmorStand stand;

    public SpongeArmorStandInventory(final SpongeArmorStand stand) {
        this.stand = stand;
    }

    @Override
    public Collection<Item> items() {
        final List<ItemStack> items = HartshornUtils.emptyList();
        this.stand.entity().present(entity -> {
            items.add(entity.itemInHand(HandTypes.MAIN_HAND));
            items.add(entity.itemInHand(HandTypes.OFF_HAND));
            items.add(entity.head());
            items.add(entity.chest());
            items.add(entity.legs());
            items.add(entity.feet());
        });

        return items.stream()
                .map(SpongeAdapter::fromSponge)
                .map(Item.class::cast)
                .toList();
    }

    @Override
    public boolean give(final Item item) {
        this.slot(item, Slot.MAIN_HAND);
        return true;
    }

    @Override
    public Item slot(final Slot slot) {
        return this.stand.entity()
                .map(entity -> SpongeAdapter.fromSponge(switch (slot) {
                    case HELMET -> entity.head();
                    case CHESTPLATE -> entity.chest();
                    case LEGGINGS -> entity.legs();
                    case BOOTS -> entity.feet();
                    case MAIN_HAND -> entity.itemInHand(HandTypes.MAIN_HAND);
                    case OFF_HAND -> entity.itemInHand(HandTypes.OFF_HAND);
                }))
                .map(Item.class::cast)
                .orElse(() -> Item.of(this.applicationContext(), ItemTypes.AIR))
                .get();
    }

    @Override
    public void slot(final Item item, final Slot slot) {
        this.stand.entity().present(entity -> {
            final ItemStack itemStack = SpongeAdapter.toSponge(item);
            final EquipmentType type = SpongeAdapter.toSponge(slot);
            entity.equipment().slot(type).ifPresent(equipmentSlot -> equipmentSlot.set(itemStack));
        });
    }
}
