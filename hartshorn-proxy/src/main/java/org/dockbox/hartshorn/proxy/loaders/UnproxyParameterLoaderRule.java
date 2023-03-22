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

package org.dockbox.hartshorn.proxy.loaders;

import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.proxy.Unproxy;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderRule;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.option.Option;

public class UnproxyParameterLoaderRule implements ParameterLoaderRule<ProxyParameterLoaderContext> {

    @Override
    public boolean accepts(final ParameterView<?> parameter, final int index, final ProxyParameterLoaderContext context, final Object... args) {
        return parameter.annotations().has(Unproxy.class) || parameter.declaredBy().annotations().has(Unproxy.class);
    }

    @Override
    public <T> Option<T> load(final ParameterView<T> parameter, final int index, final ProxyParameterLoaderContext context, final Object... args) {
        final Object argument = args[index];
        final Option<ProxyManager<Object>> handler = context.applicationProxier().manager(argument);
        return handler.flatMap(ProxyManager::delegate).orCompute(() -> {
            final Unproxy unproxy = parameter.annotations().get(Unproxy.class).orCompute(() -> parameter.declaredBy().annotations().get(Unproxy.class).orNull()).get();
            if (unproxy.fallbackToProxy()) return argument;
            else return null;
        }).map(parameter.type()::cast);
    }
}
