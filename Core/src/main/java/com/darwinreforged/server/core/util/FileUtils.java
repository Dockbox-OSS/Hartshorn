package com.darwinreforged.server.core.util;

import com.darwinreforged.server.core.init.AbstractUtility;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.modules.ModuleInfo;
import com.darwinreforged.server.core.modules.PluginModuleNative;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AbstractUtility("Common utilities for file management and parsing")
public abstract class FileUtils {

    private static final Yaml yaml = new Yaml();

    public void writeYaml(Map<String, Object> data, File file) {
        try {
            FileWriter writer = new FileWriter(file);
            yaml.dump(data, writer);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public <I extends PluginModuleNative> void writeYaml(Map<String, Object> data, I module) {
        writeYaml(data, getYamlConfigFile(module));
    }

    public Map<String, Object> getYamlData(File file) {
        try {
            FileReader reader = new FileReader(file);
            return yaml.loadAs(reader, Map.class);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return new HashMap<>();
    }

    public <I extends PluginModuleNative> Map<String, Object> getYamlData(I module) {
        return getYamlData(getYamlConfigFile(module));
    }

    public abstract <I extends PluginModuleNative> Path getDataDirectory(I module);

    public <I extends PluginModuleNative> Path getDataDirectory(I module, String dir) {
        Path ddir = getDataDirectory(module);
        return createPathIfNotExist(new File(ddir.toFile(), dir).toPath());
    }

    protected Path createPathIfNotExist(Path path) {
        if (!path.toFile().exists()) path.toFile().mkdirs();
        return path;
    }

    public abstract Path getModuleDirectory();

    public abstract <I extends PluginModuleNative> Path getConfigDirectory(I module);

    public abstract Path getLogDirectory();

    public <I extends PluginModuleNative> File getYamlConfigFile(I module) {
        return getYamlConfigFile(module, true);
    }

    public <I extends PluginModuleNative> File getYamlConfigFile(I module, boolean createIfNotExists) {
        Path path = getConfigDirectory(module);
        Optional<ModuleInfo> info = DarwinServer.getModuleInfo(module.getClass());
        if (info.isPresent()) {
            String moduleId = info.get().id();
            File file = new File(path.toFile(), String.format("%s.yml", moduleId));
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
        throw new RuntimeException("No such module registered");
    }

}
