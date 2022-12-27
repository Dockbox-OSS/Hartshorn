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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.beans.BeanContext;
import org.dockbox.hartshorn.beans.BeanObserver;
import org.dockbox.hartshorn.beans.BeanProvider;
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
public class CommandBeanListener implements BeanObserver {

    @Override
    public void onBeansCollected(final ApplicationContext applicationContext, final BeanContext beanContext) {
        final BeanProvider provider = beanContext.provider();

        final List<ArgumentConverter<?>> argumentConverters = TypeUtils.adjustWildcards(provider.all(ArgumentConverter.class), List.class);
        final ContextKey<ArgumentConverterContext> converterContextContextKey = ContextKey.builder(ArgumentConverterContext.class)
                .fallback(ArgumentConverterContext::new)
                .build();

        final ArgumentConverterContext converterContext = applicationContext.first(converterContextContextKey).get();
        argumentConverters.forEach(converterContext::register);

        final ContextKey<CommandExtensionContext> commandExtensionContextKey = ContextKey.builder(CommandExtensionContext.class)
                .fallback(CommandExtensionContext::new)
                .build();
        final CommandExtensionContext extensionContext = applicationContext.first(commandExtensionContextKey).get();

        for (final CommandExecutorExtension extension : provider.all(CommandExecutorExtension.class)) {
            applicationContext.log().debug("Adding extension " + extension.getClass().getSimpleName() + " to command gateway");
            extensionContext.add(extension);
        }
    }
}
