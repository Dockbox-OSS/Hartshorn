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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.commands.context.ArgumentConverterContext;
import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.component.processing.ProcessingOrder;
import org.dockbox.hartshorn.component.processing.ServicePreProcessor;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.Exceptional;
import org.dockbox.hartshorn.util.reflect.FieldContext;

import java.util.List;

/**
 * Processes any service with static {@link ArgumentConverter} fields, and registers them to
 * the {@link ArgumentConverterContext} contained in the {@link ApplicationContext}. Requires
 * the presence of {@link UseCommands}.
 */
public class ArgumentServicePreProcessor implements ServicePreProcessor<UseCommands> {

    @Override
    public boolean preconditions(final ApplicationContext context, final Key<?> key) {
        return !key.type().fieldsOf(ArgumentConverter.class).isEmpty();
    }

    @Override
    public <T> void process(final ApplicationContext context, final Key<T> key) {
        final List<FieldContext<ArgumentConverter>> fields = key.type().fieldsOf(ArgumentConverter.class);
        context.log().debug("Found %d argument converters in %s".formatted(fields.size(), key.type().qualifiedName()));
        context.first(ArgumentConverterContext.class).map(converterContext -> {
            for (final FieldContext<ArgumentConverter> field : fields) {
                if (field.isStatic()) {
                    final Exceptional<ArgumentConverter> converter = field.getStatic();
                    converter.present(converterContext::register);
                }
                else {
                    throw new ApplicationException(field.name() + " should be static");
                }
            }
            return null;
        }).rethrowUnchecked();
    }

    @Override
    public Integer order() {
        return ProcessingOrder.FIRST;
    }
}
