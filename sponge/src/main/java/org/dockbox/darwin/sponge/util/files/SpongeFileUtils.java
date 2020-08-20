package org.dockbox.darwin.sponge.util.files;

import org.dockbox.darwin.core.util.files.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class SpongeFileUtils implements FileUtils {

    @NotNull
    @Override
    public Path createPathIfNotExists(@NotNull Path path) {
        if (!path.toFile().exists()) path.toFile().mkdirs();
        return path;
    }

    @NotNull
    @Override
    public File createFileIfNotExists(@NotNull File file) {
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

    @NotNull
    @Override
    public Path getDataDir() {
        return getServerRoot().resolve("data/");
    }

    @NotNull
    @Override
    public Path getLogsDir() {
        return createPathIfNotExists(getServerRoot().resolve("logs/"));
    }

    @NotNull
    @Override
    public Path getServerRoot() {
        Path modDir = Sponge.getGame().getGameDirectory().toAbsolutePath();
        return createPathIfNotExists(modDir);
    }

    @NotNull
    @Override
    public Path getModuleDir() {
        return createPathIfNotExists(getServerRoot().resolve("modules/"));
    }

    @NotNull
    @Override
    public Path getModDir() {
        return createPathIfNotExists(getServerRoot().resolve("mods/"));
    }

    @NotNull
    @Override
    public Path getPluginDir() {
        return createPathIfNotExists(getServerRoot().resolve("plugins/"));
    }

    @NotNull
    @Override
    public Path getModuleConfigDir() {
        return getServerRoot().resolve("config/modules/");
    }

    @NotNull
    @Override
    public Path getModConfigDir() {
        return getServerRoot().resolve("config/");
    }

    @NotNull
    @Override
    public Path getPluginConfigDir() {
        return getServerRoot().resolve("config/plugins/");
    }
}
