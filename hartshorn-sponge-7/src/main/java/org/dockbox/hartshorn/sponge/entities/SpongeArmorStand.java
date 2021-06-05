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

package org.dockbox.hartshorn.sponge.entities;

import com.flowpowered.math.vector.Vector3d;

import net.minecraft.entity.item.EntityArmorStand;

import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.di.annotations.Wired;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.entities.ArmorStand;
import org.dockbox.hartshorn.server.minecraft.inventory.Inventory;
import org.dockbox.hartshorn.sponge.util.SpongeConversionUtil;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;

import java.util.Map;

import lombok.Getter;

public class SpongeArmorStand extends SpongeCloneableEntity<EntityArmorStand, ArmorStand> implements ArmorStand {

    // Creates cached bindings for the limb keys
    private static final Map<Limbs, Key<Value<Vector3d>>> limbKeys = HartshornUtils.ofEntries(
            HartshornUtils.entry(Limbs.HEAD, Keys.HEAD_ROTATION),
            HartshornUtils.entry(Limbs.LEFT_ARM, Keys.LEFT_ARM_ROTATION),
            HartshornUtils.entry(Limbs.RIGHT_ARM, Keys.RIGHT_ARM_ROTATION),
            HartshornUtils.entry(Limbs.LEFT_LEG, Keys.LEFT_LEG_ROTATION),
            HartshornUtils.entry(Limbs.RIGHT_LEG, Keys.RIGHT_LEG_ROTATION));

    @Getter
    private final org.spongepowered.api.entity.living.ArmorStand representation;

    public SpongeArmorStand(org.spongepowered.api.entity.living.ArmorStand representation) {
        this.representation = representation;
    }

    @Wired
    public SpongeArmorStand(Location location) {
        this.representation = super.create(location);
    }

    @Override
    protected EntityType getEntityType() {
        return EntityTypes.ARMOR_STAND;
    }

    @Override
    protected ArmorStand from(Entity clone) {
        return new SpongeArmorStand((org.spongepowered.api.entity.living.ArmorStand) clone);
    }

    @Override
    public Vector3N getRotation(Limbs limb) {
        if (Limbs.BODY == limb) {
            return SpongeConversionUtil.fromSponge(this.representation.getBodyPartRotationalData().bodyRotation().get());
        }
        else {
            Key<Value<Vector3d>> rotationKey = limbKeys.get(limb);
            return this.getRepresentation()
                    .get(rotationKey)
                    .map(SpongeConversionUtil::fromSponge)
                    .orElse(Vector3N.empty());
        }
    }

    @Override
    public void setRotation(Limbs limb, Vector3N rotation) {
        if (Limbs.BODY == limb) {
            this.representation
                    .getBodyPartRotationalData()
                    .bodyRotation()
                    .set(SpongeConversionUtil.toSponge(rotation));
        }
        else {
            Key<Value<Vector3d>> rotationKey = limbKeys.get(limb);
            this.getRepresentation().offer(rotationKey, SpongeConversionUtil.toSponge(rotation));
        }
    }

    @Override
    public boolean hasBaseplate() {
        return this.representation.basePlate().get();
    }

    @Override
    public void setBaseplate(boolean baseplate) {
        this.representation.basePlate().set(baseplate);
    }

    @Override
    public boolean isSmall() {
        return this.getRepresentation().getOrElse(Keys.ARMOR_STAND_IS_SMALL, false);
    }

    @Override
    public void setSmall(boolean small) {
        this.getRepresentation().offer(Keys.ARMOR_STAND_IS_SMALL, small);
    }

    @Override
    public boolean hasArms() {
        return this.getRepresentation().getOrElse(Keys.ARMOR_STAND_HAS_ARMS, false);
    }

    @Override
    public void setArms(boolean arms) {
        this.getRepresentation().offer(Keys.ARMOR_STAND_HAS_ARMS, arms);
    }

    @Override
    public Inventory getInventory() {
        //noinspection ReturnOfNull
        return null; // TODO: Implement entity inventory
    }
}