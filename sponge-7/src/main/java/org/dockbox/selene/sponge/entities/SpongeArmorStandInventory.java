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

package org.dockbox.selene.sponge.entities;

import org.dockbox.selene.api.entities.ArmorStandInventory;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.inventory.Slot;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.common.objects.inventory.AbstractInventoryRow;
import org.dockbox.selene.sponge.objects.inventory.SpongeInventory;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.spongepowered.api.item.inventory.equipment.EquipmentInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult.Type;

import java.util.Collection;

public class SpongeArmorStandInventory extends ArmorStandInventory implements SpongeInventory {

    private final SpongeArmorStand owner;

    public SpongeArmorStandInventory(SpongeArmorStand owner) {
        this.owner = owner;
    }

    @Override
    public Collection<Item> getAllItems() {
        return this.getAllItems(this.getReference());
    }

    @Override
    public boolean give(Item item) {
        return this.getReference().offer(SpongeConversionUtil.toSponge(item)).getType() == Type.SUCCESS;
    }

    @Override
    public Item getSlot(Slot slot) {
        Exceptional<org.spongepowered.api.item.inventory.Slot> inventorySlot = this.internalGetSlot(slot);
        return inventorySlot.map(SLOT_LOOKUP).orElseGet(AbstractInventoryRow.AIR);
    }

    @Override
    public void setSlot(Item item, Slot slot) {
        this.internalGetSlot(slot).ifPresent(inventorySlot -> inventorySlot.set(SpongeConversionUtil.toSponge(item)));
    }

    private Exceptional<org.spongepowered.api.item.inventory.Slot> internalGetSlot(Slot slot) {
        EquipmentInventory inventory = this.getReference();
        EquipmentType equipmentType = SpongeConversionUtil.toSponge(slot);
        return Exceptional.of(inventory.getSlot(equipmentType));
    }

    private EquipmentInventory getReference() {
        return (EquipmentInventory) this.owner.getRepresentation().getInventory();
    }
}
