package com.darwinreforged.servermodifications.util.todo;

import com.darwinreforged.servermodifications.util.PluginUtils;
import org.spongepowered.api.Sponge;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    private static final Yaml yaml = new Yaml();

    public static void writeYaml(Map<String, Object> data, File file) {
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

    public static Map<String, Object> getYamlData(File file) {
        try {
            FileReader reader = new FileReader(file);
            return yaml.loadAs(reader, Map.class);
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        return new HashMap<>();
    }

    public static Map<String, Object> getYamlData(Object plugin) {
        return getYamlData(getYamlConfigFile(plugin));
    }


    public static Path getDataDirectory(Object plugin) {
        String pluginId = PluginUtils.getPluginId(plugin);
        return Sponge.getGame().getSavesDirectory().resolve("data/" + pluginId);
    }

    public static Path getConfigDirectory(Object plugin) {
        return Sponge.getConfigManager().getPluginConfig(plugin).getConfigPath();
    }

    public static  File getYamlConfigFile(Object plugin) {
        Path path = getConfigDirectory(plugin);
        String pluginId = PluginUtils.getPluginId(plugin);
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
