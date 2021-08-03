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

package org.dockbox.hartshorn.persistence.dialects.sqlite;

import org.dockbox.hartshorn.api.domain.FileTypes;
import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.annotations.inject.Named;
import org.dockbox.hartshorn.di.properties.InjectorProperty;
import org.dockbox.hartshorn.persistence.JooqSqlService;
import org.dockbox.hartshorn.persistence.SqlService;
import org.dockbox.hartshorn.persistence.exceptions.InvalidConnectionException;
import org.dockbox.hartshorn.persistence.properties.PathProperty;
import org.jooq.SQLDialect;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Binds(value = SqlService.class, named = @Named(value = FileTypes.SQLITE))
public class JooqSqliteService extends JooqSqlService<Path> {

    private Path filePath;

    @Override
    public boolean canEnable() {
        return null == this.filePath && super.canEnable();
    }

    @Override
    protected Path defaultTarget() {
        return this.filePath;
    }

    @Override
    protected Connection connection(Path target) throws InvalidConnectionException {
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + target.toFile().getAbsolutePath());
        }
        catch (SQLException e) {
            throw new InvalidConnectionException(
                    "Could not open file '" + target.toFile().getAbsolutePath(), e);
        }
    }

    @Override
    public void enable() {
        if (this.filePath == null) throw new IllegalArgumentException("Missing target file path");
    }

    @Override
    protected SQLDialect dialect() {
        return SQLDialect.SQLITE;
    }

    @Override
    public void apply(InjectorProperty<?> property) throws ApplicationException {
        super.apply(property);
        if (property instanceof PathProperty pathProperty) {
            this.filePath = pathProperty.value();
        }
    }
}
