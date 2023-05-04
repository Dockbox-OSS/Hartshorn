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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.contextual.StaticBinds;
import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.commands.arguments.CommandParameterLoader;
import org.dockbox.hartshorn.commands.extension.CooldownExtension;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoader;

import jakarta.inject.Singleton;

@Service
@RequiresActivator(UseCommands.class)
public class CommandProviders {

    @Binds
    public Class<? extends CommandListener> listener = CommandListenerImpl.class;

    @Binds
    @Singleton
    public Class<? extends SystemSubject> systemSubject = ApplicationSystemSubject.class;

    @Binds
    @Singleton
    public Class<? extends CommandGateway> commandGateway = CommandGatewayImpl.class;

    @Binds
    public Class<? extends CommandParser> commandParser = CommandParserImpl.class;

    @StaticBinds
    public static CooldownExtension cooldownExtension(final ApplicationContext applicationContext) {
        return new CooldownExtension(applicationContext);
    }

    @Binds("command_loader")
    public ParameterLoader<?> parameterLoader() {
        return new CommandParameterLoader();
    }
}
