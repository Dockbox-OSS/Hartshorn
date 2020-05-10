package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.init.AbstractUtility;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.modules.Module;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AbstractUtility("Common utilities for file management and parsing")
public abstract class FileUtils {

    private static final Yaml yaml = new Yaml();

    /*
     * Data files (SQLite)
     */
    public abstract SQLDataTable getSQLData(File file);

    public abstract SQLDataTable getSQLData(Object module);

    public File getSQLDataFile(Object module) {
        return getSQLDataFile(module, true);
    }

    public File getSQLDataFile(Object module, boolean createIfNotExists) {
        Path path = getDataDirectory(module);
        Optional<Module> info = DarwinServer.getModuleInfo(module.getClass());
        if (info.isPresent()) {
            String moduleId = info.get().id();
            File file = new File(path.toFile(), String.format("%s.dat", moduleId));
            return createFileIfNotExists(file, createIfNotExists);
        }
        throw new RuntimeException("No such module registered");
    }

    public abstract void writeSQLData(SQLDataTable data, File file);

    public abstract void writeSQLData(SQLDataTable data, Object module);

    public abstract void updateSQLData(SQLDataTable data, File file);

    public abstract void updateSQLData(SQLDataTable data, Object module);

    public abstract void deleteSQLData(SQLDataTable data, File file);

    public abstract void deleteSQLData(SQLDataTable data, Object module);

    private void ensureExists(SQLDataTable table, Object module) {

    }

    public static class SQLDataTable {
        private final String table;
        private final String[] columnNames;

        // TODO : Include this in documentation
        // Each list entry is a row, each map is the column name and its value
        // list: [
        // map1:
        //   {
        //      key: 'column'
        //      value: 'value'
        //   }
        // etc..

        private final List<Map<String, Object>> data;
        final List<String> ids = new ArrayList<>();

        public SQLDataTable(String table, String[] columnNames, List<Map<String, Object>> data) {
            this.columnNames = columnNames;
            this.table = table;

            data.forEach(this::verifyData);
            this.data = data;
        }

        public void addRow(Map<String, Object> row) {
            verifyData(row);
            data.add(row);
        }

        private void verifyData(Map<String, Object> row) {
            int columns = columnNames.length;
            if (row.size() != columns) throw new IllegalArgumentException("Row length cannot be different from amount of columns");
            if (!row.containsKey("id")) throw new IllegalArgumentException("All rows should contain unique ID's");
            else {
                String id = row.get("id").toString();
                if (ids.contains(id)) throw new IllegalArgumentException("All rows should contain unique ID's");
                else ids.add(id);
            }
        }

        public String getTable() {
            return table;
        }

        public String[] getColumnNames() {
            return columnNames;
        }

        public List<Map<String, Object>> getData() {
            return data;
        }

        public List<String> getIds() {
            return ids;
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
