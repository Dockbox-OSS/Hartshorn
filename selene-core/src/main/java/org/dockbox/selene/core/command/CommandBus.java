/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.command;

import org.dockbox.selene.core.command.registry.ClassCommandRegistration;
import org.dockbox.selene.core.command.registry.MethodCommandRegistration;
import org.dockbox.selene.core.i18n.permissions.AbstractPermission;
import org.dockbox.selene.core.objects.optional.Exceptional;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.UUID;

public interface CommandBus {

    void register(Object... objs);
    void registerSingleMethodCommands(Class<?> clazz);
    void registerClassCommand(Class<?> clazz, Object instance);

    ClassCommandRegistration createClassRegistration(Class<?> clazz);
    MethodCommandRegistration[] createSingleMethodRegistrations(Collection<Method> methods);

    void registerCommand(String command, AbstractPermission permission, CommandRunner runner);
    Exceptional<Boolean> confirmLastCommand(UUID uuid);

}
