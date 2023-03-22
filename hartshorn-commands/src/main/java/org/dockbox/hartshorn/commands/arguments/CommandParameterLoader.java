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

import org.dockbox.hartshorn.commands.context.CommandParameterContext;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.util.RuleBasedParameterLoader;

import java.util.ArrayList;

public class CommandParameterLoader extends RuleBasedParameterLoader<CommandParameterLoaderContext> {

    public CommandParameterLoader() {
        this.add(new CommandContextParameterRule());
        this.add(new CommandSubjectParameterRule());
    }

    @Override
    protected <T> T loadDefault(final ParameterView<T> parameter, final int index, final CommandParameterLoaderContext context, final Object... args) {
        final CommandParameterContext parameterContext = new ArrayList<>(context.executorContext().parameters().values()).get(index);
        final T out = context.commandContext().get(parameterContext.parameter().name(), parameter.type().type());
        return out == null ? super.loadDefault(parameter, index, context, args) : out;
    }
}
