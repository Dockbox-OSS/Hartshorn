package org.dockbox.darwin.core.server

import org.dockbox.darwin.core.annotations.Module
import java.util.function.Function

abstract class ServerReference {

    open fun <T> getInstance(type: Class<T>): T {
        return Server.getInstance(type)
    }

    open fun getModule(module: Class<*>?): Module? {
        if (module == null) return null;
        return module.getAnnotation(Module::class.java) ?: getModule(module.superclass)
    }

    open fun <T> getModuleAndCallback(module: Class<*>, consumer: Function<Module, T>): T {
        val annotation = getModule(module) ?: throw IllegalArgumentException("Requested module is not annotated as such")
        return consumer.apply(annotation)
    }
}
