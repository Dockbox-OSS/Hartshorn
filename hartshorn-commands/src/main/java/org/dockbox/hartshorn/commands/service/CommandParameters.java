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

package org.dockbox.hartshorn.commands.service;

import org.dockbox.hartshorn.commands.annotations.Parameter;
import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.commands.arguments.CustomParameterPattern;
import org.dockbox.hartshorn.commands.arguments.DynamicPatternConverter;
import org.dockbox.hartshorn.commands.context.ArgumentConverterContext;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.activate.AutomaticActivation;
import org.dockbox.hartshorn.core.annotations.activate.UseBootstrap;
import org.dockbox.hartshorn.core.boot.ApplicationManager;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.services.ComponentPreProcessor;
import org.dockbox.hartshorn.core.services.ProcessingOrder;

/**
 * Scans for any type annotated with {@link Parameter} and registers a {@link DynamicPatternConverter}
 * for each type found. Requires the use of a {@link ApplicationManager} and
 * presence of {@link UseBootstrap}.
 */
@AutomaticActivation
public class CommandParameters implements ComponentPreProcessor<UseCommands> {

    @Override
    public Class<UseCommands> activator() {
        return UseCommands.class;
    }

    @Override
    public boolean modifies(final ApplicationContext context, final Key<?> key) {
        return key.type().annotation(Parameter.class).present();
    }

    @Override
    public <T> void process(final ApplicationContext context, final Key<T> key) {
        final Parameter meta = key.type().annotation(Parameter.class).get();
        final CustomParameterPattern pattern = context.get(meta.pattern());
        final String parameterKey = meta.value();
        final ArgumentConverter<?> converter = new DynamicPatternConverter<>(key.type(), pattern, parameterKey);
        context.first(ArgumentConverterContext.class).present(converterContext -> converterContext.register(converter));
    }

    @Override
    public Integer order() {
        return ProcessingOrder.EARLY;
    }
}
