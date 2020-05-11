package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.init.AbstractUtility;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.modules.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AbstractUtility("Common utilities for file management and parsing")
public abstract class FileUtils {

    private final ObjectMapper mapper;
    private static final Map<String, ConnectionSource> jdbcSources = new HashMap<>();
    private static final String JDBC_FORMAT = "jdbc:sqlite:%s";

    public FileUtils() {
        YAMLFactory factory = new YAMLFactory().disable(Feature.WRITE_DOC_START_MARKER);
        mapper = new ObjectMapper(factory);
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public <T, I> Dao<T, I> getDataDb(Class<T> object, Class<I> idType, File file) {
        if (isConnected(file)) {
            ConnectionSource source = jdbcSources.get(file.toString());
            try {
                TableUtils.createTableIfNotExists(source, object);
                return DaoManager.createDao(source, object);
            } catch (SQLException e) {
                DarwinServer.error("Could not create dao for object", e);
            }
        }
        return null;
    }

    public <T, I> Dao<T, I> getDataDb(Class<T> object, Class<I> idType, Object module) {
        Optional<Module> info;
        if (module instanceof Class) info = DarwinServer.getModuleInfo((Class<?>) module);
        else info = DarwinServer.getModuleInfo(module.getClass());

        if (info.isPresent()) {
            String id = info.get().id();
            File file = new File(getDataDirectory(module).toFile(), String.format("%s.dat", id));
            return getDataDb(object, idType, file);
        }
        throw new RuntimeException("No such module registered");
    }

    public boolean isConnected(File file) {
        String fileAb = file.toString();
        if (jdbcSources.containsKey(fileAb) && jdbcSources.get(fileAb) != null) return true;
        String connStr = String.format(JDBC_FORMAT, fileAb);
        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(connStr);
            jdbcSources.put(fileAb, connectionSource);
            return true;
        } catch (SQLException e) {
            DarwinServer.error(String.format("Failed to create connection source for '%s'", fileAb), e);
            return false;
        }
    }

    /*
     * Config files (YAML)
     */
    public <T> T getYamlDataFromFile(File file, Class<T> type, T defaultValue) {
        try {
            T res = mapper.readValue(file, type);
            return res != null ? res : defaultValue;
        } catch (IOException e) {
            DarwinServer.error("Failed to get YAML data from file", e);
        }
        return defaultValue;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Map<String, Object> getYamlDataFromFile(File file) {
        return (Map<String, Object>) getYamlDataFromFile(file, Map.class, new HashMap());
    }

    public Map<String, Object> getYamlDataForConfig(Object module) {
        return getYamlDataFromFile(getYamlConfigFile(module));
    }

    @SuppressWarnings("unchecked")
    public <T> T getYamlDataForConfig(Object module, String path, Class<T> type) {
        Map<String, Object> values = getYamlDataForConfig(module);
        if (values.containsKey(path)) {
            Object val = values.get(path);
            if (val.getClass().isAssignableFrom(type) || val.getClass().equals(type))
                return (T) val;
        }

        try {
            return type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
    }

    public File getYamlConfigFile(Object module) {
        return getYamlConfigFile(module, true);
    }

    public File getYamlConfigFile(Object module, boolean createIfNotExists) {
        Path path = getConfigDirectory(module);
        Optional<Module> info;
        if (module instanceof Class) info = DarwinServer.getModuleInfo((Class<?>) module);
        else info = DarwinServer.getModuleInfo(module.getClass());

        if (info.isPresent()) {
            String moduleId = info.get().id();
            File file = new File(path.toFile(), String.format("%s.yml", moduleId));
            return createIfNotExists ? createFileIfNotExists(file) : file;
        }
        throw new RuntimeException("No such module registered");
    }

    public <T> void writeYamlDataToFile(T data, File file) {
        try {
            mapper.writeValue(file, data);
        } catch (IOException e) {
            DarwinServer.error("Failed to write YAML data to file", e);
        }
    }

    public void writeYamlDataForConfig(Map<String, Object> data, Object module) {
        writeYamlDataToFile(data, getYamlConfigFile(module));
    }


    /*
     * Default directories
     */
    public abstract Path getModuleDirectory();

    public abstract Path getConfigDirectory(Object module);

    public abstract Path getLogDirectory();

    public abstract Path getDataDirectory(Object module);

    public Path getDataDirectory(Object module, String dir) {
        Path ddir = getDataDirectory(module);
        return createPathIfNotExist(new File(ddir.toFile(), dir).toPath());
    }

    /*
     * Path and file existence validation
     */
    public File createFileIfNotExists(File file) {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return file;
    }

    protected Path createPathIfNotExist(Path path) {
        if (!path.toFile().exists()) path.toFile().mkdirs();
        return path;
    }
}
