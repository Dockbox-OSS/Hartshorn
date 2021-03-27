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

import org.dockbox.selene.api.entities.ArmorStand;
import org.dockbox.selene.api.objects.inventory.Inventory;
import org.dockbox.selene.api.objects.tuple.Vector3N;
import org.dockbox.selene.api.util.SeleneUtils;

import java.util.Map;
import java.util.UUID;

public class JUnitArmorStand extends JUnitLivingEntity<ArmorStand> implements ArmorStand {

    private final Map<Limbs, Vector3N> limbs = SeleneUtils.emptyMap();

    public JUnitArmorStand(UUID uuid) {
        super(uuid);
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
        return false;
    }

    @Override
    public void setBaseplate(boolean baseplate) {

    }

    @Override
    public boolean isSmall() {
        return false;
    }

    @Override
    public void setSmall(boolean small) {

    }

    @Override
    public boolean hasArms() {
        return false;
    }

    @Override
    public void setArms(boolean arms) {

    }

    @Override
    public ArmorStand copy() {
        return null;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
