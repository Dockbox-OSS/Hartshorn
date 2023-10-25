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
import org.dockbox.hartshorn.component.contextual.StaticComponentContext;
import org.dockbox.hartshorn.component.contextual.StaticComponentObserver;
import org.dockbox.hartshorn.component.contextual.StaticComponentProvider;
import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.commands.context.ArgumentConverterContext;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.commands.extension.CommandExecutorExtension;
import org.dockbox.hartshorn.commands.extension.CommandExtensionContext;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.context.ContextKey;
import org.dockbox.hartshorn.util.TypeUtils;

import java.util.List;

@Service
@RequiresActivator(UseCommands.class)
public class CommandStaticComponentListener implements StaticComponentObserver {

    @Override
    public void onStaticComponentsCollected(ApplicationContext applicationContext, StaticComponentContext staticComponentContext) {
        StaticComponentProvider provider = staticComponentContext.provider();

        List<ArgumentConverter<?>> argumentConverters = TypeUtils.adjustWildcards(provider.all(ArgumentConverter.class), List.class);
        ContextKey<ArgumentConverterContext> converterContextContextKey = ContextKey.builder(ArgumentConverterContext.class)
                .fallback(ArgumentConverterContext::new)
                .build();

        ArgumentConverterContext converterContext = applicationContext.first(converterContextContextKey).get();
        argumentConverters.forEach(converterContext::register);

        ContextKey<CommandExtensionContext> commandExtensionContextKey = ContextKey.builder(CommandExtensionContext.class)
                .fallback(CommandExtensionContext::new)
                .build();
        CommandExtensionContext extensionContext = applicationContext.first(commandExtensionContextKey).get();

        for (CommandExecutorExtension extension : provider.all(CommandExecutorExtension.class)) {
            applicationContext.log().debug("Adding extension " + extension.getClass().getSimpleName() + " to command gateway");
            extensionContext.add(extension);
        }
    }
}
