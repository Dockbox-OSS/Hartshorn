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
import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.di.annotations.component.Component;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.di.properties.AttributeHolder;
import org.dockbox.hartshorn.persistence.SqlService;
import org.dockbox.hartshorn.persistence.properties.ConnectionAttribute;
import org.dockbox.hartshorn.persistence.properties.PathAttribute;
import org.dockbox.hartshorn.regions.CustomRegion;
import org.dockbox.hartshorn.regions.flags.PersistentFlagModel;
import org.dockbox.hartshorn.regions.flags.RegionFlag;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import lombok.Getter;

@Component
public class RegionsList implements AttributeHolder {

    private SqlService sqlService;
    @Getter private final Set<PersistentFlagModel> flags = HartshornUtils.emptySet();
    private final List<CustomRegion> regions = HartshornUtils.emptyList();

    public void add(RegionFlag<?> flag) {
        this.flags.add(flag.model());
        this.withSql(sql -> sql.save(flag.model()));
    }

    public void add(CustomRegion element) {
        this.regions.add(element);
        for (RegionFlag<?> flag : element.flags().keySet()) {
            this.add(flag);
        }
        this.withSql(sql -> {
            final PersistentRegion model = element.model();
            sql.save(model);
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
        return Hartshorn.context().get(RegionsList.class, new PathAttribute(file));
    }

    @Override
    public void apply(Attribute<?> property) {
        if (property instanceof PathAttribute pathAttribute) {
            final Path path = pathAttribute.value();
            this.sqlService = Hartshorn.context().get(SqlService.class, ConnectionAttribute.of(path));
        }
    }
}
