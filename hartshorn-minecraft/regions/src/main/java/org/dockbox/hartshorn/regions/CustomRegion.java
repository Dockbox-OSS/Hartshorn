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
import org.dockbox.hartshorn.api.events.annotations.Posting;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.persistence.PersistentCapable;
import org.dockbox.hartshorn.regions.events.CancellableRegionEvent;
import org.dockbox.hartshorn.regions.events.flags.RegionFlagAddedEvent;
import org.dockbox.hartshorn.regions.events.flags.RegionFlagRemovedEvent;
import org.dockbox.hartshorn.regions.flags.RegionFlag;
import org.dockbox.hartshorn.server.minecraft.dimension.Worlds;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.server.minecraft.players.Players;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Map;
import java.util.UUID;

import lombok.Getter;

@Posting({ RegionFlagAddedEvent.class, RegionFlagRemovedEvent.class })
public class CustomRegion implements Region, PersistentCapable<PersistentRegion> {

    @Getter private Vector3N cornerA;
    @Getter private Vector3N cornerB;

    private transient Vector3N size;

    private String name;

    private UUID owner;
    private UUID world;

    private final Map<String, SerializedFlag> flags = HartshornUtils.emptyMap();

    protected CustomRegion() {
    }

    public CustomRegion(Text name, Vector3N cornerA, Vector3N cornerB, UUID owner, UUID world) {
        this.name = name.toLegacy();

        this.cornerA = HartshornUtils.minimumPoint(cornerA, cornerB);
        this.cornerB = HartshornUtils.maximumPoint(cornerA, cornerB);

        this.size = HartshornUtils.cuboidSize(this.cornerA, this.cornerB);

        this.owner = owner;
        this.world = world;
    }

    @Override
    public Text name() {
        if (this.name == null) {
            this.name = "Unnamed";
        }
        return Text.of(this.name);
    }

    @Override
    public Exceptional<Player> owner() {
        return Hartshorn.context().get(Players.class).player(this.owner);
    }

    @Override
    public Map<RegionFlag<?>, ?> flags() {
        Map<RegionFlag<?>, Object> flags = HartshornUtils.emptyMap();

        for (SerializedFlag serializedFlag : this.flags.values()) {
            final Exceptional<RegionFlag<?>> flag = serializedFlag.restoreFlag().rethrow();
            if (flag.absent()) {
                Hartshorn.log().warn("Could not restore flag with ID '" + serializedFlag.id() + "'");
                continue;
            }

            final Object value = serializedFlag.restoreValue().orNull();
            flags.put(flag.get(), value);
        }
        return flags;
    }

    @Override
    public <T> void add(RegionFlag<T> flag, T value) {
        final CancellableRegionEvent event = new RegionFlagAddedEvent(this, flag).post();
        if (!event.cancelled())
            this.flags.put(flag.id(), new SerializedFlag(flag, value));
    }

    @Override
    public void remove(RegionFlag<?> flag) {
        final CancellableRegionEvent event = new RegionFlagRemovedEvent(this, flag).post();
        if (!event.cancelled())
            this.flags.remove(flag.id());
    }

    @Override
    public <T> Exceptional<T> get(RegionFlag<T> flag) {
        return Exceptional.of(() -> this.flags.getOrDefault(flag.id(), null))
                .flatMap(SerializedFlag::restoreValue);
    }

    @Override
    public Location center() {
        final Vector3N center = HartshornUtils.centerPoint(this.cornerA(), this.cornerB());
        return Location.of(center, this.world());
    }

    @Override
    public Vector3N size() {
        if (this.size == null) {
            this.size = HartshornUtils.cuboidSize(this.cornerA(), this.cornerB());
        }
        return this.size;
    }

    @Override
    public World world() {
        return Hartshorn.context().get(Worlds.class).world(this.world).or(World.empty());
    }

    @Override
    public Class<? extends PersistentRegion> modelType() {
        return PersistentRegion.class;
    }

    @Override
    public PersistentRegion model() {
        return new PersistentRegion(this.name().toLegacy(), this.owner.toString(), this.world.toString(),
                this.cornerA().xI(), this.cornerA().yI(), this.cornerA().zI(),
                this.cornerB().xI(), this.cornerB().yI(), this.cornerB().zI());
    }
}
