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

import net.minecraft.entity.item.EntityItemFrame;

import org.dockbox.selene.core.PlatformConversionService;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.nms.entities.NMSItemFrame;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

public class SpongeItemFrame extends NMSItemFrame {

    ItemFrame representation;

    @Override
    public Exceptional<Item> getDisplayedItem() {
        return Exceptional.of(this.representation.get(Keys.REPRESENTED_ITEM)
                .map(ItemStackSnapshot::createStack)
                .map(PlatformConversionService::map));
    }

    @Override
    public void setDisplayedItem(Item stack) {
        this.representation.offer(Keys.REPRESENTED_ITEM, PlatformConversionService.<Item, ItemStack>map(stack).createSnapshot());
    }

    @Override
    public Rotation getRotation() {
        return this.representation.get(Keys.ROTATION)
                .map(PlatformConversionService::map)
                .map(Rotation.class::cast)
                .orElse(Rotation.TOP);
    }

    @Override
    public void setRotation(Rotation rotation) {
        this.representation.offer(Keys.ROTATION, PlatformConversionService.map(rotation));
    }

    @Override
    public EntityItemFrame getEntity() {
        return (EntityItemFrame) this.representation;
    }
}
