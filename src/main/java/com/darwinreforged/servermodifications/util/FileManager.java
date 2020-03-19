package com.darwinreforged.servermodifications.util;

import org.spongepowered.api.Sponge;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    private final Yaml yaml = new Yaml();

    public void writeYaml(Map<String, Object> data, File file) {
        try {
            FileWriter writer = new FileWriter(file);
            yaml.dump(data, writer);
        } catch (IOException ex) {
      System.out.println(ex.getMessage());
        }
    }

    public void writeYaml(Map<String, Object> data, Object plugin) {
        writeYaml(data, getYamlConfigFile(plugin));
    }

    public Map<String, Object> getYamlData(File file) {
        try {
            FileReader reader = new FileReader(file);
            return yaml.loadAs(reader, Map.class);
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        return new HashMap<>();
    }

    public Map<String, Object> getYamlData(Object plugin) {
        return getYamlData(getYamlConfigFile(plugin));
    }


    public Path getDataDirectory(Object plugin) {
        String pluginId = CommonUtils.getPluginId(plugin);
        return Sponge.getGame().getSavesDirectory().resolve("data/" + pluginId);
    }

    public Path getConfigDirectory(Object plugin) {
        return Sponge.getConfigManager().getPluginConfig(plugin).getConfigPath();
    }

    public File getYamlConfigFile(Object plugin) {
        Path path = getConfigDirectory(plugin);
        String pluginId = CommonUtils.getPluginId(plugin);
        File file = new File(path.toFile(), String.format("%s.yml", pluginId));
        if (!file.exists()) {
            try {
                file.mkdirs();
                file.createNewFile();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return file;
    }

}
