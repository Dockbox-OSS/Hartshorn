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
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.annotations.inject.Bound;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.entities.ArmorStand;
import org.dockbox.hartshorn.server.minecraft.entities.ArmorStandInventory;
import org.dockbox.hartshorn.sponge.util.SpongeAdapter;
import org.dockbox.hartshorn.sponge.util.SpongeUtil;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.math.vector.Vector3d;

import java.util.Map;

@Binds(ArmorStand.class)
public class SpongeArmorStand
        extends SpongeCloneableEntityReference<ArmorStand, net.minecraft.world.entity.decoration.ArmorStand, org.spongepowered.api.entity.living.ArmorStand>
        implements ArmorStand {

    private static final Map<Limbs, Key<Value<Vector3d>>> limbs = HartshornUtils.<Limbs, Key<Value<Vector3d>>>mapBuilder()
            .add(Limbs.HEAD, Keys.HEAD_ROTATION)
            .add(Limbs.LEFT_ARM, Keys.LEFT_ARM_ROTATION)
            .add(Limbs.RIGHT_ARM, Keys.RIGHT_ARM_ROTATION)
            .add(Limbs.LEFT_LEG, Keys.LEFT_LEG_ROTATION)
            .add(Limbs.RIGHT_LEG, Keys.RIGHT_LEG_ROTATION)
            .get();

    @Bound
    public SpongeArmorStand(Location location) {
        super(location);
    }

    public SpongeArmorStand(org.spongepowered.api.entity.living.ArmorStand entity) {
        super(entity);
    }

    @Override
    public Vector3N rotation(Limbs limb) {
        return SpongeUtil.get(this.entity(), limbs.get(limb), SpongeAdapter::fromSponge, Vector3N::empty);
    }

    @Override
    public void rotation(Limbs limb, Vector3N rotation) {
        this.entity().present(entity -> entity.offer(limbs.get(limb), SpongeAdapter.toSpongeDouble(rotation)));
    }

    @Override
    public boolean baseplate() {
        return SpongeUtil.get(this.entity(), Keys.HAS_BASE_PLATE, b -> b, () -> false);
    }

    @Override
    public SpongeArmorStand baseplate(boolean baseplate) {
        this.entity().present(entity -> entity.offer(Keys.HAS_BASE_PLATE, baseplate));
        return this;
    }

    @Override
    public boolean small() {
        return SpongeUtil.get(this.entity(), Keys.IS_SMALL, b -> b, () -> false);
    }

    @Override
    public SpongeArmorStand small(boolean small) {
        this.entity().present(entity -> entity.offer(Keys.IS_SMALL, small));
        return this;
    }

    @Override
    public boolean arms() {
        return SpongeUtil.get(this.entity(), Keys.HAS_ARMS, b -> b, () -> false);
    }

    @Override
    public SpongeArmorStand arms(boolean arms) {
        this.entity().present(entity -> entity.offer(Keys.HAS_ARMS, arms));
        return this;
    }

    @Override
    public ArmorStandInventory inventory() {
        return new SpongeArmorStandInventory(this);
    }

    @Override
    public ArmorStand from(org.spongepowered.api.entity.living.ArmorStand entity) {
        return new SpongeArmorStand(entity);
    }

    @Override
    public EntityType<org.spongepowered.api.entity.living.ArmorStand> type() {
        return EntityTypes.ARMOR_STAND.get();
    }

    @Override
    public Exceptional<org.spongepowered.api.entity.living.ArmorStand> spongeEntity() {
        return this.entity();
    }
}
