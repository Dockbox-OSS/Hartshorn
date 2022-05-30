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

package org.dockbox.hartshorn.events.handle;

import org.dockbox.hartshorn.util.reflect.ParameterContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.parameter.ParameterLoaderRule;

public class EventParameterRule implements ParameterLoaderRule<EventParameterLoaderContext> {
    @Override
    public boolean accepts(final ParameterContext<?> parameter, final int index, final EventParameterLoaderContext context, final Object... args) {
        return TypeContext.of(context.event()).childOf(parameter.type());
    }

    @Override
    public <T> Result<T> load(final ParameterContext<T> parameter, final int index, final EventParameterLoaderContext context, final Object... args) {
        return Result.of((T) context.event());
    }
}
