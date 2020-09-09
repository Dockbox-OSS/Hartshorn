/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.darwin.core.command

import org.dockbox.darwin.core.command.registry.ClassCommandRegistration
import org.dockbox.darwin.core.command.registry.MethodCommandRegistration
import org.dockbox.darwin.core.i18n.permissions.AbstractPermission
import java.lang.reflect.Method

interface CommandBus {

    fun register(vararg objs: Any)
    fun registerSingleMethodCommand(clazz: Class<*>)
    fun registerClassCommand(clazz: Class<*>, instance: Any)

    fun createClassRegistration(clazz: Class<*>): ClassCommandRegistration
    fun createSingleMethodRegistrations(methods: Collection<Method>): Array<MethodCommandRegistration>

    fun registerCommand(command: String, permission: AbstractPermission, runner: CommandRunnerFunction)

}
