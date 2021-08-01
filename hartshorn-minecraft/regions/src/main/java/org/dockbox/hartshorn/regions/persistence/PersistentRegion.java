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

package org.dockbox.hartshorn.regions.persistence;

import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.persistence.PersistentModel;
import org.dockbox.hartshorn.regions.CustomRegion;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PersistentRegion implements PersistentModel<CustomRegion> {

    private long id;

    private String name;
    private String owner;
    private String world;

    private int corner_a_x;
    private int corner_a_y;
    private int corner_a_z;

    private int corner_b_x;
    private int corner_b_y;
    private int corner_b_z;

    @Override
    public Class<? extends CustomRegion> type() {
        return CustomRegion.class;
    }

    @Override
    public CustomRegion restore() {
        return new CustomRegion(Text.of(this.name),
                Vector3N.of(this.corner_a_x, this.corner_a_y, this.corner_a_z),
                Vector3N.of(this.corner_b_x, this.corner_b_y, this.corner_b_z),
                UUID.fromString(this.owner), UUID.fromString(this.world));
    }
}
