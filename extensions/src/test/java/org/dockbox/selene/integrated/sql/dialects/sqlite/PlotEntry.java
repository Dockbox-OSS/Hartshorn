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

package org.dockbox.selene.integrated.sql.dialects.sqlite;

import org.dockbox.selene.core.annotations.entity.Property;

public class PlotEntry {

    @Property("plot_id_x")
    private int plotX;
    @Property("plot_id_z")
    private int plotZ;
    @Property(value = "owner", setter = "setOwner")
    private String owner;

    private int id;
    private String world;

    public int getPlotX() {
        return this.plotX;
    }

    public int getPlotZ() {
        return this.plotZ;
    }

    public String getOwner() {
        return this.owner;
    }

    public int getId() {
        return this.id;
    }

    public String getWorld() {
        return this.world;
    }

    public void setOwner(String ownerId) {
        this.owner = owner;
    }
}
