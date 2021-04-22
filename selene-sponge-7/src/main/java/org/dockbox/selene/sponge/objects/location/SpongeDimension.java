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

package org.dockbox.selene.sponge.objects.location;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

import org.dockbox.selene.api.entities.Entity;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.server.minecraft.item.Item;
import org.dockbox.selene.minecraft.dimension.world.BlockDimension;
import org.dockbox.selene.minecraft.dimension.world.EntityHolding;
import org.dockbox.selene.minecraft.dimension.position.BlockFace;
import org.dockbox.selene.api.objects.profile.Profile;
import org.dockbox.selene.api.domain.tuple.Vector3N;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.sponge.objects.SpongeProfile;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.extent.Extent;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface SpongeDimension extends BlockDimension, EntityHolding {

    @Override
    default Vector3N minimumPosition() {
        return SpongeConversionUtil.fromSponge(this.getExtent().getBlockMin().toDouble());
    }

    @Override
    default Vector3N maximumPosition() {
        return SpongeConversionUtil.fromSponge(this.getExtent().getBlockMin().toDouble());
    }

    @Override
    default Vector3N floor(Vector3N position) {
        Vector3i floor = this.getExtent().getHighestPositionAt(SpongeConversionUtil.toSponge(position).toInt());
        return SpongeConversionUtil.fromSponge(floor.toDouble());
    }

    @Override
    default boolean hasBlock(Vector3N position) {
        Vector3d loc = SpongeConversionUtil.toSponge(position);
        return this.getExtent().containsBlock(loc.toInt());
    }

    @Override
    default Exceptional<Item> getBlock(Vector3N position) {
        Vector3d loc = SpongeConversionUtil.toSponge(position);
        BlockState blockState = this.getExtent().getBlock(loc.toInt());
        if (blockState.getType() == BlockTypes.AIR) return Exceptional.of(Selene.getItems().getAir());
        ItemStack stack = ItemStack.builder().fromBlockState(blockState).build();
        return Exceptional.of(SpongeConversionUtil.fromSponge(stack));
    }

    @Override
    default boolean setBlock(Vector3N position, Item item, BlockFace direction, Profile placer) {
        Vector3d loc = SpongeConversionUtil.toSponge(position);
        Optional<BlockType> blockType = SpongeConversionUtil.toSponge(item).getType().getBlock();
        if (!blockType.isPresent()) return false;
        BlockState state = blockType.get().getDefaultState();
        Direction dir = SpongeConversionUtil.toSponge(direction);
        GameProfile profile = null;
        if (placer instanceof SpongeProfile) {
            profile = ((SpongeProfile) placer).getGameProfile();
        }
        return this.getExtent().placeBlock(loc.toInt(), state, dir, profile);
    }

    @Override
    default Collection<Entity> getEntities() {
        return this.getExtent().getEntities().stream().map(SpongeConversionUtil::fromSponge).collect(Collectors.toList());
    }

    @Override
    default Collection<Entity> getEntities(Predicate<Entity> predicate) {
        return this.getExtent().getEntities(entity -> {
            Entity seleneEntity = SpongeConversionUtil.fromSponge(entity);
            return predicate.test(seleneEntity);
        }).stream().map(SpongeConversionUtil::fromSponge).collect(Collectors.toList());
    }

    Extent getExtent();
}
