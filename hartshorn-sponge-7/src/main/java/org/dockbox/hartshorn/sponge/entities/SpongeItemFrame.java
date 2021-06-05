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

package org.dockbox.hartshorn.sponge.entities;

import net.minecraft.entity.item.EntityItemFrame;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.annotations.Wired;
import org.dockbox.hartshorn.server.minecraft.dimension.position.BlockFace;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.entities.ItemFrame;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.sponge.util.SpongeConversionUtil;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.util.Direction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SpongeItemFrame extends SpongeCloneableEntity<EntityItemFrame, ItemFrame> implements ItemFrame {

    private final org.spongepowered.api.entity.hanging.ItemFrame representation;

    @Wired
    public SpongeItemFrame(Location location) {
        this.representation = super.create(location);
    }

    @Override
    public Exceptional<Item> getDisplayedItem() {
        return Exceptional.of(this.getRepresentation()
                .get(Keys.REPRESENTED_ITEM)
                .map(ItemStackSnapshot::createStack)
                .map(SpongeConversionUtil::fromSponge));
    }

    @Override
    public void setDisplayedItem(Item stack) {
        if (stack.isAir()) this.getRepresentation().remove(Keys.REPRESENTED_ITEM);
        else
            this.getRepresentation()
                    .offer(Keys.REPRESENTED_ITEM, SpongeConversionUtil.toSponge(stack).createSnapshot());
    }

    @Override
    public Rotation getRotation() {
        return this.getRepresentation()
                .get(Keys.ROTATION)
                .map(SpongeConversionUtil::fromSponge)
                .orElse(Rotation.TOP);
    }

    @Override
    public void setRotation(Rotation rotation) {
        this.getRepresentation().offer(Keys.ROTATION, SpongeConversionUtil.toSponge(rotation));
    }

    @Override
    public BlockFace getBlockFace() {
        Direction direction = this.getRepresentation().getOrElse(Keys.DIRECTION, Direction.NONE);
        return SpongeConversionUtil.fromSponge(direction);
    }

    @Override
    public void setBlockFace(BlockFace blockFace) {
        this.getRepresentation().offer(Keys.DIRECTION, SpongeConversionUtil.toSponge(blockFace));
    }

    @Override
    protected EntityType getEntityType() {
        return EntityTypes.ITEM_FRAME;
    }

    @Override
    protected ItemFrame from(Entity clone) {
        return new SpongeItemFrame((org.spongepowered.api.entity.hanging.ItemFrame) clone);
    }
}