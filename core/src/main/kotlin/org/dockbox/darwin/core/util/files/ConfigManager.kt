package org.dockbox.darwin.core.util.files

import java.io.File
import java.nio.file.Path

interface ConfigManager {

    fun getConfigDir(module: Class<*>): Path
    fun getConfigDir(module: Any): Path

    fun getConfigFile(module: Class<*>): File
    fun getConfigFile(module: Any): File

    fun getConfigContents(module: Class<*>): Map<String, Any>
    fun getConfigContents(module: Any): Map<String, Any>

    fun <T> getConfigContents(module: Class<*>, convertTo: Class<T>, defaultValue: T): T
    fun <T> getConfigContents(module: Any, convertTo: Class<T>, defaultValue: T): T

    fun writeToConfig(module: Class<*>, data: Map<String, Any>)
    fun writeToConfig(module: Any, data: Map<String, Any>)

    fun <T> writeToConfig(module: Class<*>, data: T)
    fun <T> writeToConfig(module: Any, data: T)


}
