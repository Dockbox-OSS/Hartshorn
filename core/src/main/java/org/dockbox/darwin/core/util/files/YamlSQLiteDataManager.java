package org.dockbox.darwin.core.util.files;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.dockbox.darwin.core.server.Server;
import org.dockbox.darwin.core.server.ServerReference;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class YamlSQLiteDataManager extends ServerReference implements DataManager<Dao<?, ?>> {

    private final ObjectMapper mapper;
    private static final Map<String, ConnectionSource> jdbcSources = new ConcurrentHashMap<>();
    private static final String JDBC_FORMAT = "jdbc:sqlite:%s";

    public YamlSQLiteDataManager() {
        YAMLFactory fact = new YAMLFactory();
        fact.disable(Feature.WRITE_DOC_START_MARKER);
        mapper = new ObjectMapper(fact);

        mapper.findAndRegisterModules();
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.WRAP_ROOT_VALUE);
        mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
    }

    @NotNull
    @Override
    public Path getDataDir(@NotNull Class<?> module) {
        return getModuleAndCallback(module, (annotation) -> getInstance(FileUtils.class).getDataDir().resolve(annotation.id()));
    }

    @NotNull
    @Override
    public Path getDataDir(@NotNull Object module) {
        if (!(module instanceof Class)) module = module.getClass();
        return getDataDir((Class<?>) module);
    }

    @NotNull
    @Override
    public File getDefaultDataFile(@NotNull Class<?> module) {
        return getModuleAndCallback(module, (annotation) -> {
            Path dataPath = getDataDir(module);
            return getInstance(FileUtils.class).createFileIfNotExists(new File(dataPath.toFile(), annotation.id() + ".yml"));
        });
    }

    @NotNull
    @Override
    public File getDefaultDataFile(@NotNull Object module) {
        if (!(module instanceof Class)) module = module.getClass();
        return getDefaultDataFile((Class<?>) module);
    }

    @NotNull
    @Override
    public File getDefaultBulkDataFile(@NotNull Class<?> module) {
        return getModuleAndCallback(module, (annotation) -> {
            Path dataPath = getDataDir(module);
            return getInstance(FileUtils.class).createFileIfNotExists(new File(dataPath.toFile(), annotation.id() + ".db"));
        });
    }

    @NotNull
    @Override
    public File getDefaultBulkDataFile(@NotNull Object module) {
        if (!(module instanceof Class)) module = module.getClass();
        return getDefaultBulkDataFile((Class<?>) module);
    }

    @Override
    public Dao<?, ?> getBulkDao(@NotNull Class<?> module, @NotNull Class<?> type, @NotNull String fileName) {
        return getModuleAndCallback(module, (annotation) -> {
            Path dataDir = getDataDir(module);
            return constructDao(type, new File(dataDir.toFile(), fileName + ".sqlite"));
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

    @SuppressWarnings({"rawtypes", "unchecked"})
    @NotNull
    @Override
    public Map<String, Object> getDataContents(@NotNull Class<?> module) {
        return getDataContents(module, Map.class, new HashMap());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @NotNull
    @Override
    public Map<String, Object> getDataContents(@NotNull Object module) {
        return getDataContents(module, Map.class, new HashMap());
    }

    @NotNull
    @Override
    public <T> T getDataContents(@NotNull Class<?> module, @NotNull Class<T> convertTo, T defaultValue) {
        return getModuleAndCallback(module, (annotation) -> {
            try {
                File cf = getDefaultDataFile(module);
                T res = mapper.readValue(cf, convertTo);
                return res != null ? res : defaultValue;
            } catch (IOException | IllegalArgumentException e) {
                Server.getServer().except("Failed to map data contents", e);
            }
            return defaultValue;
        });
    }

    @NotNull
    @Override
    public <T> T getDataContents(@NotNull Object module, @NotNull Class<T> convertTo, T defaultValue) {
        if (!(module instanceof Class)) module = module.getClass();
        return getDataContents((Class<?>) module, convertTo, defaultValue);
    }

    @Override
    public <T> void writeToData(@NotNull Class<?> module, T data) {
        try {
            File df = getDefaultDataFile(module);
            mapper.writeValue(df, data);
        } catch (IOException e) {
            Server.getServer().except("Failed to write data contents", e);
        }
    }

    @Override
    public <T> void writeToData(@NotNull Object module, T data) {
        if (!(module instanceof Class)) module = module.getClass();
        writeToData((Class<?>) module, data);
    }

    @Override
    public <T> void writeToData(@NotNull Class<?> module, T data, @NotNull String fileName) {
        try {
            Path dataDir = getDataDir(module);
            File df = getInstance(FileUtils.class).createFileIfNotExists(new File(dataDir.toFile(), fileName + ".yml"));
            mapper.writeValue(df, data);
        } catch (IOException e) {
            Server.getServer().except("Failed to write data contents", e);
        }
    }

    @Override
    public <T> void writeToData(@NotNull Object module, T data, @NotNull String fileName) {
        if (!(module instanceof Class)) module = module.getClass();
        writeToData((Class<?>) module, data, fileName);
    }

    public Dao<?, ?> constructDao(@NotNull Class<?> type, @NotNull File file) {
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

    public boolean sqliteConnectedTo(File file) {
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

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public Map<String, Object> getDataContents(@NotNull Class<?> module, @NotNull String fileName) {
        return getModuleAndCallback(module, (annotation) -> {
            try {
                File cf = getInstance(FileUtils.class).createFileIfNotExists(new File(getDefaultDataFile(module), fileName + ".yml"));
                Map<String, Object> res = mapper.readValue(cf, Map.class);
                return res != null ? res : new HashMap<>();
            } catch (IOException | IllegalArgumentException e) {
                Server.getServer().except("Failed to map data contents", e);
            }
            return new HashMap<>();
        });
    }

    @NotNull
    @Override
    public Map<String, Object> getDataContents(@NotNull Object module, @NotNull String fileName) {
        return getDataContents(module.getClass(), fileName);
    }
}
