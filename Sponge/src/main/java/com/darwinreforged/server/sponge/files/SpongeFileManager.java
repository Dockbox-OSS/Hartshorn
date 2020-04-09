package com.darwinreforged.server.sponge.files;

import com.darwinreforged.server.core.files.FileManager;
import com.darwinreforged.server.core.init.DarwinServer;
import com.darwinreforged.server.core.modules.ModuleInfo;
import com.darwinreforged.server.core.modules.PluginModuleNative;

import org.spongepowered.api.Sponge;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public class SpongeFileManager extends FileManager {
    @Override
    public <I extends PluginModuleNative> Path getDataDirectory(I plugin) {
        Optional<ModuleInfo> infoOptional = DarwinServer.getServer().getModuleInfo(plugin.getClass());
        if (infoOptional.isPresent()) {
            Path darwinDataPath = infoOptional.map(moduleInfo -> Sponge.getGame().getSavesDirectory().resolve("data/" + moduleInfo.id())).get().toAbsolutePath();
            return createPathIfNotExist(darwinDataPath);
        }
        return getConfigDirectory(plugin);
    }

    @Override
    public <I extends PluginModuleNative> Path getConfigDirectory(I plugin) {
        Optional<ModuleInfo> infoOptional = DarwinServer.getServer().getModuleInfo(plugin.getClass());
        Path darwinConfigPath = Sponge.getConfigManager().getPluginConfig(DarwinServer.getServer()).getDirectory();

        return createPathIfNotExist(infoOptional.map(moduleInfo -> new File(
                darwinConfigPath.toFile(),
                moduleInfo.id().replaceAll("\\.", "_")).toPath()).orElse(darwinConfigPath));
    }
}
