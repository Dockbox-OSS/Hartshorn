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
import org.dockbox.selene.di.annotations.AutoWired;
import org.dockbox.selene.server.minecraft.dimension.position.Location;
import org.dockbox.selene.server.minecraft.entities.ArmorStand;
import org.dockbox.selene.server.minecraft.entities.ArmorStandInventory;
import org.dockbox.selene.server.minecraft.inventory.Inventory;
import org.dockbox.selene.test.objects.inventory.JUnitArmorStandInventory;
import org.dockbox.selene.util.SeleneUtils;

import java.util.Map;
import java.util.UUID;

public class JUnitArmorStand extends JUnitEntity<ArmorStand> implements ArmorStand, org.dockbox.selene.test.objects.JUnitPersistentDataHolder {

    private boolean baseplate = true;
    private boolean arms = false;
    private boolean small = false;
    private final ArmorStandInventory inventory = new JUnitArmorStandInventory();
    private final Map<Limbs, Vector3N> limbs = SeleneUtils.emptyMap();

    public JUnitArmorStand(UUID uuid) {
        super(uuid);
    }

    @AutoWired
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
    public void setBaseplate(boolean baseplate) {
        this.baseplate = baseplate;
    }

    @Override
    public boolean isSmall() {
        return this.small;
    }

    @Override
    public void setSmall(boolean small) {
        this.small = small;
    }

    @Override
    public boolean hasArms() {
        return this.arms;
    }

    @Override
    public void setArms(boolean arms) {
        this.arms = arms;
    }

    @Override
    public ArmorStand copy() {
        return new JUnitArmorStand(UUID.randomUUID());
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}
