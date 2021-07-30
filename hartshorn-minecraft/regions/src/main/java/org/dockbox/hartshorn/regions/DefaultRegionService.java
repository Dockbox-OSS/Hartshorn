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

package org.dockbox.hartshorn.regions;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.di.properties.InjectableType;
import org.dockbox.hartshorn.di.properties.InjectorProperty;
import org.dockbox.hartshorn.persistence.FileManager;
import org.dockbox.hartshorn.regions.flags.PersistentFlagModel;
import org.dockbox.hartshorn.regions.flags.RegionFlag;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.nio.file.Path;
import java.util.Set;

@Service(id = "regions")
public class DefaultRegionService implements RegionService {

    private final RegionsList regions;
    private final Path regionsData;

    public DefaultRegionService() {
        final FileManager fileManager = Hartshorn.context().get(FileManager.class);
        this.regionsData = fileManager.data(DefaultRegionService.class).resolve("regions.sqlite");
        fileManager.createFileIfNotExists(this.regionsData);
        this.regions = RegionsList.restore(this.regionsData);
    }

    @Override
    public <R extends Region> Exceptional<R> first(Location location, Class<R> type) {
        return this.first(location.world(), location.vector(), type);
    }

    @Override
    public <R extends Region> Exceptional<R> first(Player player, Class<R> type) {
        return this.first(player.location(), type);
    }

    @Override
    public <R extends Region> Exceptional<R> first(World world, int x, int y, Class<R> type) {
        return this.first(world, Vector3N.of(x, -1, y), type);
    }

    private <R extends Region> Exceptional<R> first(World world, Vector3N position, Class<R> type) {
        if (CustomRegion.class.equals(type) || Region.class.equals(type)) {

        }
        return Exceptional.empty();
    }

    @Override
    public <R extends Region> Set<R> all(Location location, Class<R> type) {
        return this.all(location.world(), location.vector(), type);
    }

    @Override
    public <R extends Region> Set<R> all(Player player, Class<R> type) {
        return this.all(player.location(), type);
    }

    @Override
    public <R extends Region> Set<R> all(World world, int x, int y, Class<R> type) {
        return this.all(world, Vector3N.of(x, -1, y), type);
    }

    private <R extends Region> Set<R> all(World world, Vector3N position, Class<R> type) {
        if (CustomRegion.class.equals(type) || Region.class.equals(type)) {

        }
        return HartshornUtils.emptySet();
    }

    @Override
    public void register(RegionFlag<?> flag) {
        this.regions.add(flag);
    }

    @Override
    public Exceptional<RegionFlag<?>> flag(String id) {
        for (PersistentFlagModel flag : this.regions.flags()) {
            if (id.equals(flag.id())) return Exceptional.of(flag.restore());
        }
        return Exceptional.empty();
    }

    public Set<PersistentFlagModel> flags() {
        return this.regions.flags();
    }

    public void add(RegionFlag<?> flag) {
        this.regions.add(flag);
        this.regions.save(this.regionsData);
    }

    public void add(CustomRegion element) {
        this.regions.add(element);
        this.regions.save(this.regionsData);
    }
}
