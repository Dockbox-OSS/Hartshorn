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
import org.dockbox.hartshorn.server.minecraft.entities.Entity;
import org.dockbox.hartshorn.sponge.SpongeContextCarrier;
import org.dockbox.hartshorn.sponge.util.SpongeAdapter;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3i;

import java.util.Collection;
import java.util.function.Predicate;

public interface SpongeDimension extends BlockDimension, EntityHolding, SpongeContextCarrier {

    @Override
    default Collection<Entity> entities() {
        return this.entities(e -> true);
    }

    @Override
    default Collection<Entity> entities(final Predicate<Entity> predicate) {
        return this.serverWorld().entities(this.aabb(), entity -> {
            final Entity hartshornEntity = SpongeAdapter.fromSponge(entity);
            return predicate.test(hartshornEntity);
        }).stream().map(SpongeAdapter::fromSponge).toList();
    }

    private AABB aabb() {
        return AABB.of(
                SpongeAdapter.toSponge(this.minimumPosition()),
                SpongeAdapter.toSponge(this.maximumPosition())
        );
    }

    @Override
    default Vector3N minimumPosition() {
        return SpongeAdapter.fromSponge(this.serverWorld().min().toDouble());
    }

    @Override
    default Vector3N maximumPosition() {
        return SpongeAdapter.fromSponge(this.serverWorld().max().toDouble());
    }

    @Override
    default Vector3N floor(final Vector3N position) {
        final Vector3i floor = this.serverWorld().highestPositionAt(SpongeAdapter.toSponge(position).toInt());
        return SpongeAdapter.fromSponge(floor);
    }

    @Override
    default boolean has(final Vector3N position) {
        final Vector3i loc = SpongeAdapter.toSponge(position);
        return this.serverWorld().contains(loc);
    }

    @Override
    default Exceptional<Block> block(final Vector3N position) {
        final Vector3i loc = SpongeAdapter.toSponge(position);
        final BlockState blockState = this.serverWorld().block(loc);
        if (blockState.type() == BlockTypes.AIR.get()) return Exceptional.of(Block.empty(this.applicationContext()));
        return Exceptional.of(new SpongeBlock(SpongeAdapter.toSnapshot(blockState)));
    }

    @Override
    default boolean block(final Vector3N position, final Block block) {
        final Vector3i loc = SpongeAdapter.toSponge(position);
        final Exceptional<BlockState> state = SpongeAdapter.toSponge(block);
        if (state.absent()) return false;
        return this.serverWorld().setBlock(loc, state.get());
    }

    ServerWorld serverWorld();

}
