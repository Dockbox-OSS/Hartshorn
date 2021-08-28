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

package org.dockbox.hartshorn.sponge.event;

import org.dockbox.hartshorn.events.annotations.Posting;
import org.dockbox.hartshorn.server.minecraft.events.server.EngineChangedState;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerState.Loading;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerState.Reload;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerState.Started;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerState.Starting;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerState.Stopping;
import org.dockbox.hartshorn.server.minecraft.item.ItemContext;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.LoadedGameEvent;
import org.spongepowered.api.event.lifecycle.RefreshGameEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.api.registry.Registry;
import org.spongepowered.api.registry.RegistryEntry;
import org.spongepowered.api.registry.RegistryType;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.List;
import java.util.Optional;

@Posting(EngineChangedState.class)
public class ServerEventBridge extends EventBridge {

    @Listener
    public void on(final StartingEngineEvent<?> event) {
        new EngineChangedState<Starting>() {
        }.post();
    }

    @Listener
    public void on(final StartedEngineEvent<?> event) {
        final List<String> items = this.collectIdContext(RegistryTypes.ITEM_TYPE);
        final List<String> blocks = this.collectIdContext(RegistryTypes.BLOCK_TYPE);
        this.applicationContext().add(new ItemContext(items, blocks));

        new EngineChangedState<Started>() {
        }.post();
    }

    private List<String> collectIdContext(final RegistryType<?> registryType) {
        final Optional<? extends Registry<?>> itemTypeRegistry = Sponge.game().findRegistry(registryType);
        if (itemTypeRegistry.isPresent()) {
            final Registry<?> registry = itemTypeRegistry.get();
            return registry.streamEntries().map(RegistryEntry::key).map(ResourceKey::asString).toList();
        }
        else {
            this.context().log().warn("Could not collect IDs from registry " + registryType.location().asString());
        }
        return HartshornUtils.emptyList();
    }

    @Listener
    public void on(final LoadedGameEvent event) {
        new EngineChangedState<Loading>() {
        }.post();
    }

    @Listener
    public void on(final StoppingEngineEvent<?> event) {
        new EngineChangedState<Stopping>() {
        }.post();
    }

    @Listener
    public void on(final RefreshGameEvent event) {
        new EngineChangedState<Reload>() {
        }.post();
        new EngineChangedState<Loading>() {
        }.post();
    }
}
