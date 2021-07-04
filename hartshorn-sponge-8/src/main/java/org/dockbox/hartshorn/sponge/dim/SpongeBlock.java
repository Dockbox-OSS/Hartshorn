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

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.annotations.Binds;
import org.dockbox.hartshorn.di.annotations.Wired;
import org.dockbox.hartshorn.server.minecraft.dimension.Block;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.sponge.inventory.SpongeItem;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.dockbox.hartshorn.sponge.util.SpongeUtil;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.state.BooleanStateProperty;
import org.spongepowered.api.state.IntegerStateProperty;
import org.spongepowered.api.state.StateProperty;
import org.spongepowered.api.world.server.ServerLocation;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Optional;

@Binds(Block.class)
public class SpongeBlock implements Block {

    private final WeakReference<BlockSnapshot> snapshot;

    public SpongeBlock(BlockSnapshot snapshot) {
        this.snapshot = new WeakReference<>(snapshot);
    }

    @Wired
    public SpongeBlock(Item item) {
        final ItemStack itemStack = SpongeConvert.toSponge(item);
        final Optional<BlockType> block = itemStack.type().block();
        if (block.isEmpty()) this.snapshot = new WeakReference<>(BlockSnapshot.empty());
        else {
            final BlockType blockType = block.get();
            this.snapshot = new WeakReference<>(SpongeConvert.toSnapshot(blockType.defaultState()));
        }
    }

    @Wired
    public SpongeBlock(Location location) {
        final Exceptional<ServerLocation> exceptionalLocation = SpongeConvert.toSponge(location);
        if (exceptionalLocation.absent()) this.snapshot = new WeakReference<>(BlockSnapshot.empty());
        else {
            final ServerLocation serverLocation = exceptionalLocation.get();
            final BlockSnapshot snapshot = serverLocation.createSnapshot();
            this.snapshot = new WeakReference<>(snapshot);
        }
    }

    @Override
    public Exceptional<Item> item() {
        return this.state().map(BlockState::type)
                .map(BlockType::item)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(ItemStack::of)
                .map(SpongeItem::new);
    }

    @Override
    public String id() {
        return SpongeUtil.location(this.state().map(BlockState::type), RegistryTypes.BLOCK_TYPE)
                .map(ResourceKey::asString)
                .or("");
    }

    @Override
    public Map<String, Object> states() {
        return this.state().map(state -> {
            Map<String, Object> states = HartshornUtils.emptyMap();
            final Map<StateProperty<?>, ?> properties = state.statePropertyMap();
            properties.forEach((property, value) -> states.put(property.name(), value));
            return states;
        }).orElse(HartshornUtils::emptyMap).get();
    }

    @Override
    public <T> Exceptional<T> state(String state) {
        final Object value = this.states().get(state);
        //noinspection unchecked
        return Exceptional.of(() -> (T) value);
    }

    @Override
    public void state(String state, Object value) {
        this.state().present(actual -> {
            if (value instanceof Boolean b) {
                final BooleanStateProperty property = SpongeUtil.key(RegistryTypes.BOOLEAN_STATE_PROPERTY, state).get();
                actual.withStateProperty(property, b);
            }
            else if (value instanceof Integer i) {
                final IntegerStateProperty property = SpongeUtil.key(RegistryTypes.INTEGER_STATE_PROPERTY, state).get();
                actual.withStateProperty(property, i);
            }
            else if (value != null && value.getClass().isEnum()) {
                Hartshorn.log().warn("Enum states are currently not supported, if this is a custom type, store the ordinal of the enum value instead");
            }
        });
    }

    @Override
    public boolean isEmpty() {
        return this.state().map(state -> state.type().equals(BlockTypes.AIR.get())).or(false);
    }

    @Override
    public boolean place(Location location) {
        return this.state().map(state -> SpongeConvert.toSponge(location)
                .map(serverLocation -> serverLocation.setBlock(state))
                .or(false)
        ).or(false);
    }

    public Exceptional<BlockSnapshot> snapshot() {
        return Exceptional.of(this.snapshot.get());
    }

    public Exceptional<BlockState> state() {
        return this.snapshot().map(BlockSnapshot::state);
    }
}
