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

import org.dockbox.hartshorn.api.CheckedConsumer;
import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.FileTypes;
import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.di.annotations.inject.Wired;
import org.dockbox.hartshorn.di.properties.BindingMetaProperty;
import org.dockbox.hartshorn.persistence.SqlService;
import org.dockbox.hartshorn.persistence.exceptions.InvalidConnectionException;
import org.dockbox.hartshorn.persistence.properties.PathProperty;
import org.dockbox.hartshorn.persistence.table.SimpleColumnIdentifier;
import org.dockbox.hartshorn.persistence.table.Table;
import org.dockbox.hartshorn.regions.CustomRegion;
import org.dockbox.hartshorn.regions.flags.PersistentFlagModel;
import org.dockbox.hartshorn.regions.flags.RegionFlag;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import lombok.Getter;

public class RegionsList {

    @Wired SqlService sqlService;

    @Getter private final Set<PersistentFlagModel> flags = HartshornUtils.emptySet();
    private final List<CustomRegion> regions = HartshornUtils.emptyList();

    public void add(RegionFlag<?> flag) {
        this.flags.add(flag.model());
        this.withSql(sql -> {
            sql.insert("flags", Table.of(PersistentFlagModel.class, flag.model()));
        });
    }

    public void add(CustomRegion element) {
        this.regions.add(element);
        for (RegionFlag<?> flag : element.flags().keySet()) {
            this.add(flag);
        }
        this.withSql(sql -> {
            sql.insert("regions", Table.of(PersistentRegion.class, element.model()));
            sql.insertUnique("regionflags", Table.of(PersistentRegionFlag.class), new SimpleColumnIdentifier<>("region_id", String.class));
        });
    }

    private void withSql(CheckedConsumer<SqlService> consumer) {
        try {
            consumer.accept(this.sqlService);
        } catch (ApplicationException e) {
            Except.handle(e);
        }
    }

    public static RegionsList restore(Path file) {
        final RegionsList regionsList = Hartshorn.context().get(RegionsList.class);
        final SqlService sql = Hartshorn.context().get(SqlService.class, BindingMetaProperty.of(FileTypes.SQLITE), new PathProperty(file));

        try {
            regionsList.regions.addAll(sql
                    .table("regions", Table.of(PersistentRegion.class))
                    .restore(PersistentRegion.class)
            );
        }
        catch (InvalidConnectionException e) {
            Except.handle(e);
        }

        return regionsList;
    }
}
