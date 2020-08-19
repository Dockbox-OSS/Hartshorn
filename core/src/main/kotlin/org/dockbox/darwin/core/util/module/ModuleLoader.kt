package org.dockbox.darwin.core.util.module

import org.dockbox.darwin.core.annotations.Module
import org.dockbox.darwin.core.objects.module.ModuleCandidate
import org.dockbox.darwin.core.objects.module.ModuleInformation
import org.dockbox.darwin.core.objects.module.ModuleRegistration
import java.util.*

interface ModuleLoader {

    fun getModuleInformation(module: Class<*>): ModuleInformation
    fun getModuleInformation(module: String): ModuleInformation

    fun getModuleSource(module: Class<*>): String
    fun getModuleSource(module: String): String

    fun getModule(module: Class<*>): Module
    fun getModule(module: String): Module

    fun <I> getModuleInstance(module: Class<I>): Optional<I>

    fun getAllRegistrations(): Iterable<ModuleRegistration>
    fun getRegistration(module: Class<*>): ModuleRegistration
    fun getRegistration(module: String): ModuleRegistration

    fun loadCandidate(candidate: ModuleCandidate)
    fun loadCandidate(clazz: Class<*>)

}
