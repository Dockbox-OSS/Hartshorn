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

package org.dockbox.selene.test.objects.living;

import org.dockbox.selene.api.domain.tuple.Vector3N;
import org.dockbox.selene.di.annotations.Wired;
import org.dockbox.selene.server.minecraft.dimension.position.Location;
import org.dockbox.selene.server.minecraft.entities.ArmorStand;
import org.dockbox.selene.server.minecraft.entities.ArmorStandInventory;
import org.dockbox.selene.test.objects.inventory.JUnitArmorStandInventory;
import org.dockbox.selene.util.SeleneUtils;

import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public class JUnitArmorStand extends JUnitEntity<ArmorStand> implements ArmorStand, org.dockbox.selene.test.objects.JUnitPersistentDataHolder {

    @Setter
    private boolean baseplate = true;
    @Setter
    private boolean arms = false;
    @Getter @Setter
    private boolean small = false;
    @Getter
    private final ArmorStandInventory inventory = new JUnitArmorStandInventory();
    private final Map<Limbs, Vector3N> limbs = SeleneUtils.emptyMap();

    public JUnitArmorStand(UUID uuid) {
        super(uuid);
    }

    @Wired
    public JUnitArmorStand(Location location) {
        super(UUID.randomUUID());
        this.setLocation(location);
    }

    @Override
    public Vector3N getRotation(Limbs limb) {
        return this.limbs.getOrDefault(limb, Vector3N.empty());
    }

    @Override
    public void setRotation(Limbs limb, Vector3N rotation) {
        this.limbs.put(limb, rotation);
    }

    @Override
    public boolean hasBaseplate() {
        return this.baseplate;
    }

    @Override
    public boolean hasArms() {
        return this.arms;
    }

    @Override
    public ArmorStand copy() {
        return new JUnitArmorStand(UUID.randomUUID());
    }
}
