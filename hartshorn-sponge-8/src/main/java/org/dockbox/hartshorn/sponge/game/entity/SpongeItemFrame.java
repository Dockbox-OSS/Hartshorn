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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.annotations.inject.Bound;
import org.dockbox.hartshorn.server.minecraft.dimension.position.BlockFace;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.entities.ItemFrame;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.sponge.util.SpongeAdapter;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.orientation.Orientation;

@Binds(ItemFrame.class)
public class SpongeItemFrame
        extends SpongeCloneableEntityReference<ItemFrame, net.minecraft.world.entity.decoration.ItemFrame, org.spongepowered.api.entity.hanging.ItemFrame>
        implements ItemFrame {

    @Bound
    public SpongeItemFrame(Location location) {
        super(location);
    }

    public SpongeItemFrame(org.spongepowered.api.entity.hanging.ItemFrame frame) {
        super(frame);
    }

    @Override
    public Exceptional<Item> displayedItem() {
        return this.entity().map(frame -> {
            final ItemStackSnapshot snapshot = frame.item().get();
            if (snapshot.isEmpty()) return null;
            return SpongeAdapter.fromSponge(snapshot.createStack());
        });
    }

    @Override
    public SpongeItemFrame displayedItem(Item stack) {
        this.entity().present(frame -> {
            final ItemStack itemStack = SpongeAdapter.toSponge(stack);
            frame.item().set(itemStack.createSnapshot());
        });
        return this;
    }

    @Override
    public Rotation rotation() {
        return this.entity().map(frame -> {
            final Orientation orientation = frame.itemOrientation().get();
            return SpongeAdapter.fromSponge(orientation);
        }).orElse(() -> Rotation.TOP).get();
    }

    @Override
    public SpongeItemFrame rotation(Rotation rotation) {
        this.entity().present(frame -> {
            final Orientation orientation = SpongeAdapter.toSponge(rotation);
            frame.itemOrientation().set(orientation);
        });
        return this;
    }

    @Override
    public BlockFace blockFace() {
        return this.entity().map(frame -> {
            final Direction direction = frame.hangingDirection().get();
            return SpongeAdapter.fromSponge(direction);
        }).orElse(() -> BlockFace.NONE).get();
    }

    @Override
    public SpongeItemFrame blockFace(BlockFace blockFace) {
        this.entity().present(frame -> {
            final Direction direction = SpongeAdapter.toSponge(blockFace);
            frame.hangingDirection().set(direction);
        });
        return this;
    }

    @Override
    public EntityType<org.spongepowered.api.entity.hanging.ItemFrame> type() {
        return EntityTypes.ITEM_FRAME.get();
    }

    @Override
    public Exceptional<org.spongepowered.api.entity.hanging.ItemFrame> spongeEntity() {
        return this.entity();
    }

    @Override
    public ItemFrame from(org.spongepowered.api.entity.hanging.ItemFrame entity) {
        return new SpongeItemFrame(entity);
    }
}
