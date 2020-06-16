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

    fun getBulkDao(module: Class<*>, type: Class<*>, file: File): D
    fun getBulkDao(module: Any, type: Class<*>, file: File): D
    fun getBulkDao(type: Class<*>, file: File): D

    fun getDataContents(module: Class<*>): Map<String, Any>
    fun getDataContents(module: Any): Map<String, Any>

    fun <T> getDataContents(module: Class<*>, convertTo: Class<T>, defaultValue: T): T
    fun <T> getDataContents(module: Any, convertTo: Class<T>, defaultValue: T): T

    fun writeToData(module: Class<*>, data: Map<String, Any>)
    fun writeToData(module: Any, data: Map<String, Any>)

    fun <T> writeToData(module: Class<*>, data: T)
    fun <T> writeToData(module: Any, data: T)

}
