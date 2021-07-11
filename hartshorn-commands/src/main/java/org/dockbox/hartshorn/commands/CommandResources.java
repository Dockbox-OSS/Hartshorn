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

package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.api.i18n.annotations.Resource;
import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.di.annotations.service.Service;

@Service(owner = CommandGateway.class)
public interface CommandResources {

    @Resource(value = "$1This command requires confirmation, click $2[here] $1to confirm", key = "command.confirm")
    ResourceEntry getConfirmCommand();

    @Resource(value = "$1Confirm running command", key = "command.confirm.hover")
    ResourceEntry getConfirmCommandHover();

    @Resource(value = "$4This command requires arguments", key = "command.missing.arguments")
    ResourceEntry getMissingArguments();

    @Resource(value = "$4Too many arguments", key = "command.overflow")
    ResourceEntry getTooManyArguments();

    @Resource(value = "$4Not enough arguments for parameter '{0}'", key = "command.parameter.missing.arguments")
    ResourceEntry getNotEnoughParameterArguments(String parameter);

    @Resource(value = "You are in cooldown", key = "command.cooldown")
    ResourceEntry getCooldownActive();

    @Resource(value = "No supported command handler found for '{0}'", key = "command.missing.handler")
    ResourceEntry getMissingHandler(String command);

    @Resource(value = "No executor registered for command '{0}' with {1} arguments", key = "command.missing.executor")
    ResourceEntry getMissingExecutor(String alias, int size);

    @Resource(value = "Illegal argument definition", key = "command.illegal")
    ResourceEntry getIllegalArgumentDefinition();

    @Resource(value = "Unknown flag '{0}'", key = "command.flag.unknown")
    ResourceEntry getUnknownFlag(String name);

    @Resource(value = "Could not parse {0} '{1}'", key = "command.parse.failure")
    ResourceEntry getCouldNotParse(String type, String name);

    @Resource(value = "Could not execute the command as it was cancelled", key = "command.cancelled")
    ResourceEntry getCancelled();

}
