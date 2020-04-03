package com.darwinreforged.server.api.files;

import com.darwinreforged.server.api.DarwinServer;
import com.darwinreforged.server.api.modules.ModuleInfo;
import com.darwinreforged.server.api.modules.PluginModuleNative;
import com.darwinreforged.server.api.utils.PluginUtils;

import org.spongepowered.api.Sponge;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FileManager {

    private static final Yaml yaml = new Yaml();

    public static void writeYaml(Map<String, Object> data, File file) {
        try {
            FileWriter writer = new FileWriter(file);
            yaml.dump(data, writer);
        } catch (IOException ex) {
            DarwinServer.getLogger().error(ex.getMessage());
        }
    }

    public static <I extends PluginModuleNative> void writeYaml(Map<String, Object> data, I plugin) {
        writeYaml(data, getYamlConfigFile(plugin));
    }

    public static Map<String, Object> getYamlData(File file) {
        try {
            FileReader reader = new FileReader(file);
            return yaml.loadAs(reader, Map.class);
        } catch (FileNotFoundException ex) {
            DarwinServer.getLogger().error(ex.getMessage());
        }
        return new HashMap<>();
    }

    public static <I extends PluginModuleNative> Map<String, Object> getYamlData(I plugin) {
        return getYamlData(getYamlConfigFile(plugin));
    }


    public static Path getDataDirectory(Object plugin) {
        String pluginId = PluginUtils.getPluginId(plugin);
        return createPathIfNotExist(Sponge.getGame().getSavesDirectory().resolve("data/" + pluginId));
    }

    private static Path createPathIfNotExist(Path path) {
        if (!path.toFile().exists()) path.toFile().mkdirs();
        return path;
    }

    public static <I extends PluginModuleNative> Path getConfigDirectory(I plugin) {
        Optional<ModuleInfo> infoOptional = DarwinServer.getModuleInfo(plugin.getClass());
        Path darwinConfigPath = Sponge.getConfigManager().getPluginConfig(DarwinServer.getServer()).getDirectory();

        return createPathIfNotExist(infoOptional.map(moduleInfo -> new File(
                darwinConfigPath.toFile(),
                moduleInfo.id().replaceAll("\\.", "_")).toPath()).orElse(darwinConfigPath));
    }

    public static <I extends PluginModuleNative> File getYamlConfigFile(I plugin) {
        return getYamlConfigFile(plugin, true);
    }

    public static <I extends PluginModuleNative> File getYamlConfigFile(I plugin, boolean createIfNotExists) {
        Path path = getConfigDirectory(plugin);
        String pluginId = PluginUtils.getPluginId(plugin);
        File file = new File(path.toFile(), String.format("%s.yml", pluginId));
        if (!file.exists() && createIfNotExists) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException ex) {
                DarwinServer.getLogger().error(ex.getMessage());
            }
        }
        return file;
    }

}
