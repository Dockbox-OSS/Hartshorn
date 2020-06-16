package org.dockbox.darwin.core.util.files

import java.io.File
import java.nio.file.Path

interface FileUtils {

    fun getDataDir(): Path
    fun getLogsDir(): Path
    fun getServerRoot(): Path

    fun getModuleDir(): Path
    fun getModDir(): Path
    fun getPluginDir(): Path

    fun getModuleConfigDir(): Path
    fun getModConfigDir(): Path
    fun getPluginConfigDir(): Path

    fun createPathIfNotExists(path: Path): Path
    fun createFileIfNotExists(file: File): File

}
