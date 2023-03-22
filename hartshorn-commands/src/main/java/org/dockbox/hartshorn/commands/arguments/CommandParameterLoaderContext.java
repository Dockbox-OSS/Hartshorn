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

package org.dockbox.hartshorn.commands.arguments;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.context.MethodCommandExecutorContext;
import org.dockbox.hartshorn.util.ApplicationBoundParameterLoaderContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class CommandParameterLoaderContext extends ApplicationBoundParameterLoaderContext {

    private final CommandContext commandContext;
    private final MethodCommandExecutorContext<?> executorContext;

    public CommandParameterLoaderContext(final MethodView<?, ?> method, final TypeView<?> type, final Object instance, final ApplicationContext applicationContext, final CommandContext commandContext,
                                         final MethodCommandExecutorContext<?> executorContext) {
        super(method, instance, applicationContext);
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
