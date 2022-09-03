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

package org.dockbox.hartshorn.proxy.loaders;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.application.context.ParameterLoaderContext;
import org.dockbox.hartshorn.util.reflect.ParameterContext;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.function.CheckedFunction;
import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.util.parameter.ParameterLoaderRule;

public class ObjectEqualsParameterLoaderRule implements ParameterLoaderRule<ParameterLoaderContext> {
    @Override
    public boolean accepts(final ParameterContext<?> parameter, final int index, final ParameterLoaderContext context, final Object... args) {
        return parameter.declaredBy().parent().is(Object.class) && "equals".equals(parameter.declaredBy().name());
    }

    @Override
    public <T> Result<T> load(final ParameterContext<T> parameter, final int index, final ParameterLoaderContext context, final Object... args) {
        final Object argument = args[index];
        final Result<ProxyManager<Object>> handler = context.applicationContext().environment().manager().manager(argument);
        return handler.flatMap((CheckedFunction<ProxyManager<Object>, @NonNull Result<Object>>) ProxyManager::delegate).orElse(() -> argument).map(a -> (T) a);
    }
}
