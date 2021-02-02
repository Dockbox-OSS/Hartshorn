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

package org.dockbox.selene.core.entities;

import org.dockbox.selene.core.objects.targets.InventoryHolder;
import org.dockbox.selene.core.objects.tuple.Vector3N;

public interface ArmorStand extends Entity<ArmorStand>, InventoryHolder {

    Vector3N getRotation(Limbs limb);

    void setRotation(Limbs limb, Vector3N rotation);

    boolean hasBaseplate();

    void setBaseplate(boolean baseplate);

    boolean isSmall();

    void setSmall(boolean small);

    boolean hasArms();

    void setArms(boolean arms);

    enum Limbs {
        HEAD, BODY, LEFT_LEG, RIGHT_LEG, LEFT_ARM, RIGHT_ARM
    }

}
