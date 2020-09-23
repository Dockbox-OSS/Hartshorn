/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.impl.util.files;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.files.BulkDataManager;
import org.dockbox.selene.core.util.files.FileType;
import org.dockbox.selene.core.util.files.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SQLiteBulkDataManager extends BulkDataManager<Dao<?, ?>> {

    private static final Map<String, ConnectionSource> jdbcSources = new ConcurrentHashMap<>();
    private static final String JDBC_FORMAT = "jdbc:sqlite:%s";

    @NotNull
    @Override
    public Optional<Dao<?, ?>> getBulkDao(@NotNull Class<?> type, @NotNull Path file) {
        this.getInstance(FileUtils.class).createFileIfNotExists(file);
        if (this.sqliteConnectedTo(file)) {
            ConnectionSource source = jdbcSources.get(file.toString());
            try {
                TableUtils.createTableIfNotExists(source, type);
                return Optional.ofNullable(DaoManager.createDao(source, type));
            } catch (SQLException e) {
                Selene.getServer().except("Could not create DAO for type", e);
            }
        }
        return Optional.empty();
    }

    private boolean sqliteConnectedTo(Path file) {
        String fileAb = file.toString();
        if (jdbcSources.containsKey(fileAb) && jdbcSources.get(fileAb) != null) return true;
        String connStr = String.format(JDBC_FORMAT, fileAb);
        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(connStr);
            jdbcSources.put(fileAb, connectionSource);
            return true;
        } catch (SQLException e) {
            Selene.getServer().except(String.format("Failed to create connection source for '%s'", fileAb), e);
            return false;
        }
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return FileType.SQLITE;
    }
}
