/*
 * Copyright 2019-2022 the original author or authors.
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

import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.commands.arguments.CommandParameterLoader;
import org.dockbox.hartshorn.component.processing.Provider;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.util.parameter.ParameterLoader;

import javax.inject.Singleton;

@Service(activators = UseCommands.class)
public class CommandProviders {

    @Provider
    public Class<? extends CommandListener> listener() {
        return CommandListenerImpl.class;
    }

    @Provider
    @Singleton
    public Class<? extends SystemSubject> systemSubject() {
        return ApplicationSystemSubject.class;
    }

    @Provider
    @Singleton
    public Class<? extends CommandGateway> commandGateway() {
        return CommandGatewayImpl.class;
    }

    @Provider
    public Class<? extends CommandParser> commandParser() {
        return CommandParserImpl.class;
    }

    @Provider("command_loader")
    public ParameterLoader parameterLoader() {
        return new CommandParameterLoader();
    }
}