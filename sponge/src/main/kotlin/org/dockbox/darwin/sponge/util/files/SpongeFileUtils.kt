package org.dockbox.darwin.sponge.util.files

import org.dockbox.darwin.core.util.files.FileUtils
import org.spongepowered.api.Sponge
import java.io.File
import java.io.IOException
import java.nio.file.Path

class SpongeFileUtils : FileUtils {
    override fun createPathIfNotExists(path: Path): Path {
        if (!path.toFile().exists()) path.toFile().mkdirs()
        return path
    }

    override fun createFileIfNotExists(file: File): File {
        if (!file.exists()) {
            try {
                file.parentFile.mkdirs()
                file.createNewFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
        return file
    }

    override fun getDataDir(): Path {
        return getServerRoot().resolve("data/")
    }

    override fun getLogsDir(): Path {
        return createPathIfNotExists(getServerRoot().resolve("logs/"))
    }

    override fun getServerRoot(): Path {
        val modDir = Sponge.getGame().gameDirectory.toAbsolutePath()
        return createPathIfNotExists(modDir)
    }

    override fun getModuleDir(): Path {
        return createPathIfNotExists(getServerRoot().resolve("modules/"))
    }

    override fun getModDir(): Path {
        return createPathIfNotExists(getServerRoot().resolve("mods/"))
    }

    override fun getPluginDir(): Path {
        return createPathIfNotExists(getServerRoot().resolve("plugins/"))
    }

    override fun getModuleConfigDir(): Path {
        return getServerRoot().resolve("config/modules/")
    }

    override fun getModConfigDir(): Path {
        return getServerRoot().resolve("config/")
    }

    override fun getPluginConfigDir(): Path {
        return getServerRoot().resolve("config/plugins/")
    }
}
