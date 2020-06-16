package org.dockbox.darwin.core.util.files

import java.io.File
import java.nio.file.Path

interface DataManager<D> {

    fun getDataDir(module: Class<*>): Path
    fun getDataDir(module: Any): Path

    fun getDefaultDataFile(module: Class<*>): File
    fun getDefaultDataFile(module: Any): File

    fun getDefaultBulkDataFile(module: Class<*>): File
    fun getDefaultBulkDataFile(module: Any): File

    fun getDataContents(module: Class<*>): Map<String, Any>
    fun getDataContents(module: Any): Map<String, Any>

    fun <T> getDataContents(module: Class<*>, convertTo: Class<T>, defaultValue: T): T
    fun <T> getDataContents(module: Any, convertTo: Class<T>, defaultValue: T): T

    fun <T> writeToData(module: Class<*>, data: T)
    fun <T> writeToData(module: Any, data: T)
    fun <T> writeToData(module: Class<*>, data: T, fileName: String)
    fun <T> writeToData(module: Any, data: T, fileName: String)

    fun getBulkDao(module: Any, type: Class<*>, fileName: String): D
    fun getBulkDao(module: Class<*>, type: Class<*>, fileName: String): D
    fun getDefaultBulkDao(module: Any, type: Class<*>): D
    fun getDefaultBulkDao(module: Class<*>, type: Class<*>): D

}
