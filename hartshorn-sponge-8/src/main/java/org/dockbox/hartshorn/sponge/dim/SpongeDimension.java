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

package org.dockbox.hartshorn.sponge.dim;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.server.minecraft.dimension.Block;
import org.dockbox.hartshorn.server.minecraft.dimension.BlockDimension;
import org.dockbox.hartshorn.server.minecraft.dimension.EntityHolding;
import org.dockbox.hartshorn.server.minecraft.dimension.position.BlockFace;
import org.dockbox.hartshorn.server.minecraft.entities.Entity;
import org.dockbox.hartshorn.server.minecraft.players.Profile;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3i;

import java.util.Collection;
import java.util.function.Predicate;

public interface SpongeDimension extends BlockDimension, EntityHolding {

    @Override
    default Vector3N minimumPosition() {
        return SpongeConvert.fromSponge(this.serverWorld().blockMin().toDouble());
    }

    @Override
    default Vector3N maximumPosition() {
        return SpongeConvert.fromSponge(this.serverWorld().blockMax().toDouble());
    }

    @Override
    default Vector3N floor(Vector3N position) {
        Vector3i floor = this.serverWorld().highestPositionAt(SpongeConvert.toSponge(position).toInt());
        return SpongeConvert.fromSponge(floor);
    }

    @Override
    default boolean hasBlock(Vector3N position) {
        Vector3i loc = SpongeConvert.toSponge(position);
        return this.serverWorld().containsBlock(loc);
    }

    @Override
    default Exceptional<Block> getBlock(Vector3N position) {
        Vector3i loc = SpongeConvert.toSponge(position);
        BlockState blockState = this.serverWorld().block(loc);
        if (blockState.type() == BlockTypes.AIR.get()) return Exceptional.of(Block.empty());
        return Exceptional.of(SpongeConvert.fromSponge(blockState));
    }

    @Override
    default boolean setBlock(Vector3N position, Block block, BlockFace direction, Profile placer) {
        Vector3i loc = SpongeConvert.toSponge(position);
        Exceptional<BlockState> state = SpongeConvert.toSponge(block);
        if (state.absent()) return false;
        return this.serverWorld().setBlock(loc, state.get());
    }

    @Override
    default Collection<Entity> getEntities() {
        return this.getEntities(e -> true);
    }

    @Override
    default Collection<Entity> getEntities(Predicate<Entity> predicate) {
        return this.serverWorld().entities(this.aabb(), entity -> {
            Entity hartshornEntity = SpongeConvert.fromSponge(entity);
            return predicate.test(hartshornEntity);
        }).stream().map(SpongeConvert::fromSponge).toList();
    }

    private AABB aabb() {
        return AABB.of(
                SpongeConvert.toSponge(this.minimumPosition()),
                SpongeConvert.toSponge(this.maximumPosition())
        );
    }

    ServerWorld serverWorld();

}
