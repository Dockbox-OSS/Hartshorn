/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
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
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;

import java.util.UUID;

public class SpongePlayerInventory extends PlayerInventory {

    private final UUID playerUniqueId;

    public SpongePlayerInventory(UUID playerUniqueId) {
        this.playerUniqueId = playerUniqueId;
    }

    @Override
    public Item getSlot(int row, int column) {
        return getPlayer().map(player -> {

            return Item.of("");
        }).orElse(Selene.getItems().getAir());
    }

    @Override
    public Item getSlot(int index) {
        return getPlayer().map(player -> {

            return Item.of("");
        }).orElse(Selene.getItems().getAir());
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
