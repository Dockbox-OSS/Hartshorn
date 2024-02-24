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

package org.dockbox.hartshorn.commands.context;

import java.util.List;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.commands.CommandExecutor;
import org.dockbox.hartshorn.commands.CommandResources;
import org.dockbox.hartshorn.commands.arguments.CommandParameterLoaderContext;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.component.condition.ProvidedParameterContext;
import org.dockbox.hartshorn.i18n.Message;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoader;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodCommandExecutor<T> implements CommandExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(MethodCommandExecutor.class);

    private final ConditionMatcher conditionMatcher;
    private final MethodCommandExecutorContext<T> executorContext;
    private final ApplicationContext applicationContext;
    private final MethodView<T, ?> method;

    public MethodCommandExecutor(ConditionMatcher conditionMatcher, MethodCommandExecutorContext<T> context) {
        this.conditionMatcher = conditionMatcher;
        this.executorContext = context;
        this.applicationContext = context.applicationContext();
        this.method = context.method();
    }

    @Override
    public void execute(CommandContext commandContext) {
        T instance = this.applicationContext.get(this.executorContext.key());
        CommandParameterLoaderContext loaderContext = new CommandParameterLoaderContext(this.method, null, this.applicationContext, commandContext, this.executorContext);
        ParameterLoader parameterLoader = this.executorContext.parameterLoader();
        List<Object> arguments = parameterLoader.loadArguments(loaderContext);

        if (this.conditionMatcher.match(this.method, ProvidedParameterContext.of(this.method, arguments))) {
            LOG.debug("Invoking command method %s with %d arguments".formatted(this.method.qualifiedName(), arguments.size()));
            try {
                this.method.invoke(instance, arguments.toArray());
            }
            catch (Throwable throwable) {
                this.applicationContext.handle(throwable);
            }
        }
        else {
            LOG.debug("Conditions didn't match for " + this.method.qualifiedName());
            Message cancelled = this.applicationContext.get(CommandResources.class).cancelled();
            commandContext.source().send(cancelled);
        }
    }
}
