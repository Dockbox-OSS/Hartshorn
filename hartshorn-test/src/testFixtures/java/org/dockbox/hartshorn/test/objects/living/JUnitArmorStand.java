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

package org.dockbox.hartshorn.test.objects.living;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.di.annotations.inject.Bound;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.entities.ArmorStand;
import org.dockbox.hartshorn.server.minecraft.entities.ArmorStandInventory;
import org.dockbox.hartshorn.test.objects.inventory.JUnitArmorStandInventory;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

public class JUnitArmorStand extends JUnitEntity<ArmorStand> implements ArmorStand, org.dockbox.hartshorn.test.objects.JUnitPersistentDataHolder {

    @Getter private final ArmorStandInventory inventory;
    private final Map<Limbs, Vector3N> limbs = HartshornUtils.emptyMap();
    @Setter private boolean baseplate = true;
    @Setter private boolean arms = false;
    @Getter @Setter private boolean small = false;
    @Inject
    @Getter
    private ApplicationContext applicationContext;

    public JUnitArmorStand(final ApplicationContext context, final UUID uuid) {
        super(uuid);
        this.applicationContext = context;
        this.inventory = new JUnitArmorStandInventory(this.applicationContext());
    }

    @Bound
    public JUnitArmorStand(final Location location) {
        super(UUID.randomUUID());
        this.location(location);
        this.inventory = new JUnitArmorStandInventory(location.applicationContext());
    }

    @Override
    public Vector3N rotation(final Limbs limb) {
        return this.limbs.getOrDefault(limb, Vector3N.empty());
    }

    @Override
    public void rotation(final Limbs limb, final Vector3N rotation) {
        this.limbs.put(limb, rotation);
    }

    @Override
    public boolean baseplate() {
        return this.baseplate;
    }

    @Override
    public boolean arms() {
        return this.arms;
    }

    @Override
    public Exceptional<ArmorStand> copy() {
        return Exceptional.of(new JUnitArmorStand(this.applicationContext(), UUID.randomUUID()));
    }
}
