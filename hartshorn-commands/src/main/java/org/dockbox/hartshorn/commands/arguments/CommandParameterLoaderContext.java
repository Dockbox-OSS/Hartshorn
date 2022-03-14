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

package org.dockbox.hartshorn.commands.arguments;

import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.context.MethodCommandExecutorContext;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;

public class CommandParameterLoaderContext extends ParameterLoaderContext {

    private final CommandContext commandContext;
    private final MethodCommandExecutorContext<?> executorContext;

    public CommandParameterLoaderContext(final MethodContext<?, ?> method, final TypeContext<?> type, final Object instance, final ApplicationContext applicationContext, final CommandContext commandContext,
                                         final MethodCommandExecutorContext<?> executorContext) {
        super(method, type, instance, applicationContext);
        this.commandContext = commandContext;
        this.executorContext = executorContext;
    }

    public CommandContext commandContext() {
        return this.commandContext;
    }

    public MethodCommandExecutorContext<?> executorContext() {
        return this.executorContext;
    }
}
