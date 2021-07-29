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
import org.dockbox.hartshorn.api.domain.FileTypes;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.di.properties.BindingMetaProperty;
import org.dockbox.hartshorn.persistence.SQLMan;
import org.dockbox.hartshorn.persistence.dialects.sqlite.SQLitePathProperty;
import org.dockbox.hartshorn.persistence.exceptions.InvalidConnectionException;
import org.dockbox.hartshorn.persistence.table.Table;
import org.dockbox.hartshorn.persistence.table.exceptions.IdentifierMismatchException;
import org.dockbox.hartshorn.regions.flags.PersistentFlagModel;
import org.dockbox.hartshorn.regions.flags.RegionFlag;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import lombok.Getter;

public class RegionsList {

    private final List<CustomRegion> regions = HartshornUtils.emptyList();

    @Getter
    private final Set<PersistentFlagModel> flags = HartshornUtils.emptySet();

    public void add(RegionFlag<?> flag) {
        this.flags.add(flag.model());
    }

    public void add(CustomRegion element) {
        this.regions.add(element);
        for (RegionFlag<?> flag : element.flags().keySet()) {
            this.add(flag);
        }
    }

    private Table regionTable() throws IdentifierMismatchException {
        final Table table = Table.of(PersistentRegion.class);
        for (CustomRegion region : this.regions) {
            table.addRow(region);
        }
        return table;
    }

    public void save(Path file) {
        final SQLMan<?> sql = Hartshorn.context().get(SQLMan.class, BindingMetaProperty.of(FileTypes.SQLITE), new SQLitePathProperty(file));
        try {
            sql.store("regions", this.regionTable());
        }
        catch (InvalidConnectionException | IdentifierMismatchException e) {
            Except.handle(e);
        }
    }

    public static RegionsList restore(Path file) {
        final RegionsList regionsList = new RegionsList();
        final SQLMan<?> sql = Hartshorn.context().get(SQLMan.class, BindingMetaProperty.of(FileTypes.SQLITE), new SQLitePathProperty(file));

        try {
            final Table regions = sql.table("regions", Table.of(PersistentRegion.class));
            final List<CustomRegion> rows = regions.restore(PersistentRegion.class);
            regionsList.regions.addAll(rows);
        }
        catch (InvalidConnectionException e) {
            Except.handle(e);
        }

        return regionsList;
    }
}
