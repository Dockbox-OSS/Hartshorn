package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.init.AbstractUtility;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.modules.Module;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AbstractUtility("Common utilities for file management and parsing")
public abstract class FileUtils {

    private static final Yaml yaml = new Yaml();
    private static final Map<String, ConnectionSource> jdbcSources = new HashMap<>();
    private static final String JDBC_FORMAT = "jdbc:sqlite:%s";

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
        Optional<Module> info = DarwinServer.getModuleInfo(module.getClass());
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
    public Map<String, Object> getYamlData(File file) {
        try {
            FileReader reader = new FileReader(file);
            Map<String, Object> res = yaml.loadAs(reader, Map.class);
            return res != null ? res : new HashMap<>();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return new HashMap<>();
    }

    public Map<String, Object> getConfigYamlData(Object module) {
        return getYamlData(getYamlConfigFile(module));
    }

    @SuppressWarnings("unchecked")
    public <T> T getConfigYamlData(Object module, String path, Class<T> type) {
        Map<String, Object> values = getConfigYamlData(module);
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
        Optional<Module> info = DarwinServer.getModuleInfo(module.getClass());
        if (info.isPresent()) {
            String moduleId = info.get().id();
            File file = new File(path.toFile(), String.format("%s.yml", moduleId));
            return createFileIfNotExists(file, createIfNotExists);
        }
        throw new RuntimeException("No such module registered");
    }

    public void writeYaml(Map<String, Object> data, File file) {
        try {
            FileWriter writer = new FileWriter(file);
            yaml.dump(data, writer);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void writeConfigYaml(Map<String, Object> data, Object module) {
        writeYaml(data, getYamlConfigFile(module));
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
    private File createFileIfNotExists(File file, boolean createIfNotExists) {
        if (!file.exists() && createIfNotExists) {
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
