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

package org.dockbox.darwin.core.util.files;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dockbox.darwin.core.server.Server;
import org.dockbox.darwin.core.server.ServerReference;
import org.jetbrains.annotations.NotNull;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class SQLiteBulkDataManager extends ServerReference implements BulkDataManager<Dao<?, ?>> {

    private static final Map<String, ConnectionSource> jdbcSources = new ConcurrentHashMap<>();
    private static final String JDBC_FORMAT = "jdbc:sqlite:%s";

    @NotNull
    @Override
    public Path getDefaultBulkDataFile(@NotNull Class<?> module) {
        return runWithExtension(module, (annotation) -> {
            Path dataPath = getDataDir(module);
            return getInstance(FileUtils.class).createFileIfNotExists(dataPath.resolve(annotation.id() + ".db"));
        });
    }

    @NotNull
    @Override
    public Path getDefaultBulkDataFile(@NotNull Object module) {
        if (!(module instanceof Class)) module = module.getClass();
        return getDefaultBulkDataFile((Class<?>) module);
    }

    @Override
    public Dao<?, ?> getBulkDao(@NotNull Class<?> module, @NotNull Class<?> type, @NotNull String fileName) {
        return runWithExtension(module, (annotation) -> {
            Path dataDir = getDataDir(module);
            return constructDao(type, dataDir.resolve(fileName + ".sqlite"));
        });
    }

    @Override
    public Dao<?, ?> getBulkDao(@NotNull Object module, @NotNull Class<?> type, @NotNull String fileName) {
        if (!(module instanceof Class)) module = module.getClass();
        return getBulkDao((Class<?>) module, type, fileName);
    }

    @Override
    public Dao<?, ?> getDefaultBulkDao(@NotNull Class<?> module, @NotNull Class<?> type) {
        return constructDao(type, getDefaultBulkDataFile(module));
    }

    @Override
    public Dao<?, ?> getDefaultBulkDao(@NotNull Object module, @NotNull Class<?> type) {
        if (!(module instanceof Class)) module = module.getClass();
        return constructDao(type, getDefaultBulkDataFile(module));
    }

    public Dao<?, ?> constructDao(@NotNull Class<?> type, @NotNull Path file) {
        getInstance(FileUtils.class).createFileIfNotExists(file);
        if (sqliteConnectedTo(file)) {
            ConnectionSource source = jdbcSources.get(file.toString());
            try {
                TableUtils.createTableIfNotExists(source, type);
                return DaoManager.createDao(source, type);
            } catch (SQLException e) {
                Server.getServer().except("Could not create DAO for type", e);
            }
        }
        return null;
    }

    public boolean sqliteConnectedTo(Path file) {
        String fileAb = file.toString();
        if (jdbcSources.containsKey(fileAb) && jdbcSources.get(fileAb) != null) return true;
        String connStr = String.format(JDBC_FORMAT, fileAb);
        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(connStr);
            jdbcSources.put(fileAb, connectionSource);
            return true;
        } catch (SQLException e) {
            Server.getServer().except(String.format("Failed to create connection source for '%s'", fileAb), e);
            return false;
        }
    }

    @NotNull
    @Override
    public Path getDataDir(@NotNull Class<?> module) {
        return runWithExtension(module, (annotation) -> getInstance(FileUtils.class).getDataDir().resolve(annotation.id()));
    }

    @NotNull
    @Override
    public Path getDataDir(@NotNull Object module) {
        if (!(module instanceof Class)) module = module.getClass();
        return getDataDir((Class<?>) module);
    }
}
