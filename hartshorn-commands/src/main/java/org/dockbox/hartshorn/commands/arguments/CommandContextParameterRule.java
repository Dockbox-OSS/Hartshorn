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

import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderRule;

public class CommandContextParameterRule implements ParameterLoaderRule<CommandParameterLoaderContext> {

    @Override
    public boolean accepts(ParameterView<?> parameter, int index, CommandParameterLoaderContext context, Object... args) {
        TypeView<CommandContext> typeView = context.applicationContext().environment().introspector().introspect(context.commandContext());
        return typeView.isChildOf(parameter.type().type());
    }

    @Override
    public <T> Option<T> load(ParameterView<T> parameter, int index, CommandParameterLoaderContext context, Object... args) {
        return Option.of(parameter.type().cast(context.commandContext()));
    }
}
