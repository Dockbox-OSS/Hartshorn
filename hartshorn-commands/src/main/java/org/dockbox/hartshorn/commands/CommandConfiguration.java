/*
 * Copyright 2019-2024 the original author or authors.
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
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.commands.arguments.ParameterTypeArgumentConverterRegistryCustomizer;
import org.dockbox.hartshorn.commands.context.ArgumentConverterRegistry;
import org.dockbox.hartshorn.commands.context.ArgumentConverterRegistryCustomizer;
import org.dockbox.hartshorn.commands.context.SimpleArgumentConverterRegistry;
import org.dockbox.hartshorn.commands.extension.CommandExecutorExtension;
import org.dockbox.hartshorn.commands.extension.CooldownExtension;
import org.dockbox.hartshorn.component.Configuration;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.CompositeMember;
import org.dockbox.hartshorn.component.processing.Prototype;
import org.dockbox.hartshorn.component.processing.Singleton;
import org.dockbox.hartshorn.inject.Priority;
import org.dockbox.hartshorn.inject.SupportPriority;

@Configuration
@RequiresActivator(UseCommands.class)
public class CommandConfiguration {

    @Prototype
    @SupportPriority
    public CommandListener listener(ApplicationContext applicationContext, CommandGateway gateway) {
        return new CommandListenerImpl(applicationContext, gateway);
    }

    @Singleton
    @SupportPriority
    public SystemSubject systemSubject(ApplicationContext applicationContext) {
        return new ApplicationSystemSubject(applicationContext);
    }

    @Singleton
    @SupportPriority
    public CommandGateway commandGateway(
        CommandParser parser,
        CommandResources resources,
        ApplicationContext context,
        ArgumentConverterRegistry converterRegistry) {
        return new CommandGatewayImpl(parser, resources, context, converterRegistry);
    }

    @Prototype
    @SupportPriority
    public CommandParser commandParser(CommandResources resources) {
        return new CommandParserImpl(resources);
    }

    @Prototype
    @CompositeMember
    @SupportPriority
    public CommandExecutorExtension cooldownExtension(ApplicationContext applicationContext) {
        return new CooldownExtension(applicationContext);
    }

    @Prototype
    @SupportPriority
    public ArgumentConverterRegistry converterRegistry(ApplicationEnvironment environment, ArgumentConverterRegistryCustomizer customizer) {
        ArgumentConverterRegistry registry = new SimpleArgumentConverterRegistry();
        customizer.configure(registry);
        return registry;
    }

    @Prototype
    @Priority(Priority.SUPPORT_PRIORITY + 16) // Intentionally used to compose the lower priority binding
    public ArgumentConverterRegistryCustomizer converterRegistryCustomizer(ApplicationEnvironment environment, ArgumentConverterRegistryCustomizer customizer) {
        return customizer.compose(new ParameterTypeArgumentConverterRegistryCustomizer(environment));
    }
}
