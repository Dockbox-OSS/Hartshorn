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

package org.dockbox.hartshorn.proxy.loaders;

import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderRule;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A parameter loader rule that loads the first argument of an {@link Object#equals(Object)} method invocation. This
 * will attempt to unproxy the argument, and if that fails, will return the argument as-is.
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
public class ObjectEqualsParameterLoaderRule implements ParameterLoaderRule<ProxyParameterLoaderContext> {

    @Override
    public boolean accepts(ParameterView<?> parameter, int index, ProxyParameterLoaderContext context, Object... args) {
        ExecutableElementView<?> executable = parameter.declaredBy();
        return executable.declaredBy().is(Object.class) && "equals".equals(executable.name());
    }

    @Override
    public <T> Option<T> load(ParameterView<T> parameter, int index, ProxyParameterLoaderContext context, Object... args) {
        Object argument = args[index];
        Option<ProxyManager<Object>> handler = context.proxyOrchestrator().manager(argument);
        return handler.flatMap(ProxyManager::delegate)
                .orCompute(() -> argument)
                .cast(parameter.type().type());
    }
}
