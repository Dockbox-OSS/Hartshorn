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

package org.dockbox.hartshorn.sponge.util;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.registry.Registry;
import org.spongepowered.api.registry.RegistryEntry;
import org.spongepowered.api.registry.RegistryKey;
import org.spongepowered.api.registry.RegistryType;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SpongeUtil {

    public static <T> Exceptional<T> await(Future<T> future) {
        return Exceptional.of(future::get);
    }

    public static <T> Exceptional<T> awaitOption(Future<Optional<T>> future) {
        try {
            return Exceptional.of(future.get());
        }
        catch (ExecutionException | InterruptedException e) {
            return Exceptional.of(e);
        }
    }

    public static <T> T spReference(RegistryType<T> registry, String name) {
        return RegistryKey.of(registry, ResourceKey.sponge(name))
                .asDefaultedReference(() -> Sponge.game().registries())
                .get();
    }

    public static <T> Exceptional<T> fromMCRegistry(RegistryType<T> value, String name) {
        final Exceptional<Registry<T>> registry = Exceptional.of(Sponge.game().registries().findRegistry(value));
        return registry.map(r -> {
            final Optional<RegistryEntry<T>> entry = r.findEntry(ResourceKey.minecraft(name));
            return entry.map(RegistryEntry::value).orElse(null);
        });
    }

}
