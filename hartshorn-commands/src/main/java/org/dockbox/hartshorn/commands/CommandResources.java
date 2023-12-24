/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.i18n.annotations.InjectTranslation;
import org.dockbox.hartshorn.i18n.Message;

@Service
public interface CommandResources {

    @InjectTranslation(defaultValue = "$1This command requires confirmation, click $2[here] $1to confirm", key = "command.confirm")
    Message confirmCommand();

    @InjectTranslation(defaultValue = "$1Confirm running command", key = "command.confirm.hover")
    Message confirmCommandHover();

    @InjectTranslation(defaultValue = "$4This command requires arguments", key = "command.missing.arguments")
    Message missingArguments();

    @InjectTranslation(defaultValue = "$4Too many arguments", key = "command.overflow")
    Message tooManyArguments();

    @InjectTranslation(defaultValue = "$4Not enough arguments for parameter '{0}'", key = "command.parameter.missing.arguments")
    Message notEnoughParameterArguments(String parameter);

    @InjectTranslation(defaultValue = "You are in cool-down", key = "command.cooldown")
    Message cooldownActive();

    @InjectTranslation(defaultValue = "No supported command handler found for '{0}'", key = "command.missing.handler")
    Message missingHandler(String command);

    @InjectTranslation(defaultValue = "No executor registered for command '{0}' with {1} arguments", key = "command.missing.executor")
    Message missingExecutor(String alias, int size);

    @InjectTranslation(defaultValue = "Illegal argument definition", key = "command.illegal")
    Message illegalArgumentDefinition();

    @InjectTranslation(defaultValue = "Unknown flag '{0}'", key = "command.flag.unknown")
    Message unknownFlag(String name);

    @InjectTranslation(defaultValue = "Could not parse {0} '{1}'", key = "command.parse.failure")
    Message couldNotParse(String type, String name);

    @InjectTranslation(defaultValue = "Could not execute the command as it was cancelled", key = "command.cancelled")
    Message cancelled();

}
