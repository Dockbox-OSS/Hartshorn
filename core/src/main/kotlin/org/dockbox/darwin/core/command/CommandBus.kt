package org.dockbox.darwin.core.command

import org.dockbox.darwin.core.command.registry.ClassCommandRegistration
import org.dockbox.darwin.core.command.registry.MethodCommandRegistration
import org.dockbox.darwin.core.i18n.I18NRegistry
import org.dockbox.darwin.core.i18n.Permission
import java.lang.reflect.Method

interface CommandBus {

    fun register(vararg objs: Any)
    fun registerSingleMethodCommand(clazz: Class<*>)
    fun registerClassCommand(clazz: Class<*>, instance: Any)

    fun createClassRegistration(clazz: Class<*>): ClassCommandRegistration
    fun createSingleMethodRegistrations(methods: Collection<Method>): Array<MethodCommandRegistration>

    fun registerCommand(command: String, permission: I18NRegistry, runner: CommandRunnerFunction)

}
