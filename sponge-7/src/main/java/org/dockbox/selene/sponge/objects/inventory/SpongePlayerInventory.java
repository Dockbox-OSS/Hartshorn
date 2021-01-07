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

import org.dockbox.selene.core.PlayerStorageService;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.inventory.PlayerInventory;
import org.dockbox.selene.core.objects.inventory.Slot;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.player.Hand;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.sponge.objects.targets.SpongePlayer;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.common.item.inventory.query.operation.InventoryTypeQueryOperation;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class SpongePlayerInventory extends PlayerInventory {

    private static final Supplier<Item> air = () -> Selene.getItems().getAir();
    private static final Function<org.spongepowered.api.item.inventory.Slot, Item> slotLookup = slot -> {
        return slot.peek().map(SpongeConversionUtil::fromSponge)
            .map(referencedItem -> (Item) referencedItem)

            .orElseGet(air);
    };

    private final UUID playerUniqueId;

    public SpongePlayerInventory(UUID playerUniqueId) {
        this.playerUniqueId = playerUniqueId;
    }

    @Override
    public Item getSlot(int row, int column) {
        return this.getPlayer().map(player -> {
            if (3 > row) { // Main inventory
                MainPlayerInventory main = player.getInventory()
                    .query(new InventoryTypeQueryOperation(MainPlayerInventory.class));

                return main.getGrid()
                    .getSlot(column, row)
                    .map(slotLookup)
                    .orElseGet(air);

            } else if (3 == row) { // Hotbar
                Hotbar hotbar = player.getInventory()
                    .query(new InventoryTypeQueryOperation(Hotbar.class));
                return hotbar.getSlot(new SlotIndex(column))
                    .map(slotLookup)
                    .orElseGet(air);

            } else throw new IllegalArgumentException("Slot index [row="+row+", col=" + column +"] is out of bounds (row: 0-3, col: 0-8)");
        }).orElseGet(air);
    }

    @SuppressWarnings("MagicNumber")
    @Override
    public Item getSlot(int index) {
        // Supplier prevents additional performance hit
        final Supplier<Item> air = () -> Selene.getItems().getAir();
        return this.getPlayer().map(player -> {
            if (27 > index) { // Main inventory
                MainPlayerInventory main = player.getInventory()
                    .query(new InventoryTypeQueryOperation(MainPlayerInventory.class));

                return main.getGrid().getSlot(new SlotIndex(index)).map(slotLookup).orElseGet(air);

            } else if (36 > index) { // Hotbar
                Hotbar hotbar = player.getInventory()
                    .query(new InventoryTypeQueryOperation(Hotbar.class));
                // -27 to correct for the grid gap (main grid is excluded once we get the Hotbar inventory, and is 3x9 slots)
                return hotbar.getSlot(new SlotIndex(index - 27))
                    .map(slotLookup)
                    .orElseGet(air);
            } else throw new IllegalArgumentException("Slot index " + index + " is out of bounds (0-35)");
        }).orElseGet(air);
    }

    @Override
    public Item getSlot(Slot slot) {
        Exceptional<Player> lookupPlayer = this.getPlayer();
        if (lookupPlayer.isAbsent()) return Selene.getItems().getAir();
        Player player = lookupPlayer.get();
        // As we were able to look up the Sponge type player, we can confirm the player exists and can skip the isPresent check
        SpongePlayer spongePlayer = (SpongePlayer) SeleneUtils.INJECT
            .getInstance(PlayerStorageService.class)
            .getPlayer(player.getUniqueId()).get();

        EquipmentType equipmentType = SpongeConversionUtil.toSponge(slot);
        if (equipmentType == EquipmentTypes.OFF_HAND) return spongePlayer.getItemInHand(Hand.OFF_HAND);
        else if (equipmentType == EquipmentTypes.MAIN_HAND) return spongePlayer.getItemInHand(Hand.MAIN_HAND);

        Exceptional<ItemStack> stack = Exceptional.of(player.getEquipped(equipmentType));
        return stack.map(SpongeConversionUtil::fromSponge)
            .map(referencedItem -> (Item) referencedItem)
            .orElseGet(() -> Selene.getItems().getAir());
    }

    private Exceptional<Player> getPlayer() {
        return Exceptional.of(Sponge.getServer().getPlayer(this.playerUniqueId));
    }
}
