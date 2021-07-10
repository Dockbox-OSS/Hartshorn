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

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.events.annotations.Posting;
import org.dockbox.hartshorn.di.context.Context;
import org.dockbox.hartshorn.server.minecraft.dimension.BlockContext;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerReloadEvent;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerStartedEvent;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerStartingEvent;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerStoppingEvent;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerUpdateEvent;
import org.dockbox.hartshorn.server.minecraft.item.ItemContext;
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
import java.util.function.Function;

@Posting(value = {
        ServerStartedEvent.class,
        ServerStartingEvent.class,
        ServerReloadEvent.class,
        ServerUpdateEvent.class,
        ServerStoppingEvent.class
})
public class ServerEventBridge implements EventBridge {

    @Listener
    public void on(StartingEngineEvent<?> event) {
        new ServerStartingEvent().post();
    }

    @Listener
    public void on(StartedEngineEvent<?> event) {
        this.collectIdContext(RegistryTypes.ITEM_TYPE, ItemContext::new);
        this.collectIdContext(RegistryTypes.BLOCK_TYPE, BlockContext::new);
        new ServerStartedEvent().post();
    }

    @Listener
    public void on(LoadedGameEvent event) {
        new ServerUpdateEvent().post();
    }

    @Listener
    public void on(StoppingEngineEvent<?> event) {
        new ServerStoppingEvent().post();
    }

    @Listener
    public void on(RefreshGameEvent event) {
        new ServerReloadEvent().post();
        new ServerUpdateEvent().post();
    }

    private void collectIdContext(RegistryType<?> registryType, Function<List<String>, Context> storage) {
        final Optional<? extends Registry<?>> itemTypeRegistry = Sponge.game().registries().findRegistry(registryType);
        if (itemTypeRegistry.isPresent()) {
            final Registry<?> registry = itemTypeRegistry.get();
            final List<String> ids = registry.streamEntries().map(RegistryEntry::key).map(ResourceKey::asString).toList();
            Hartshorn.context().add(storage.apply(ids));
        } else {
            Hartshorn.log().warn("Could not collect IDs from registry " + registryType.location().asString());
        }
    }
}
