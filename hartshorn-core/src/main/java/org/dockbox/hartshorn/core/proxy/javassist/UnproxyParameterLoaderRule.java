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

package org.dockbox.hartshorn.core.proxy.javassist;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.core.annotations.Unproxy;
import org.dockbox.hartshorn.core.context.ParameterLoaderContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.function.CheckedFunction;
import org.dockbox.hartshorn.core.proxy.ProxyManager;
import org.dockbox.hartshorn.core.services.parameter.ParameterLoaderRule;

public class UnproxyParameterLoaderRule implements ParameterLoaderRule<ParameterLoaderContext> {
    @Override
    public boolean accepts(final ParameterContext<?> parameter, final int index, final ParameterLoaderContext context, final Object... args) {
        return parameter.annotation(Unproxy.class).present() || parameter.declaredBy().annotation(Unproxy.class).present();
    }

    @Override
    public <T> Exceptional<T> load(final ParameterContext<T> parameter, final int index, final ParameterLoaderContext context, final Object... args) {
        final Object argument = args[index];
        final Exceptional<ProxyManager<Object>> handler = context.applicationContext().environment().manager().manager(argument);
        return handler.flatMap((CheckedFunction<ProxyManager<Object>, @NonNull Exceptional<Object>>) ProxyManager::delegate).orElse(() -> {
            final Unproxy unproxy = parameter.annotation(Unproxy.class).orElse(() -> parameter.declaredBy().annotation(Unproxy.class).orNull()).get();
            if (unproxy.fallbackToProxy()) return argument;
            else return null;
        }).map(a -> (T) a);
    }
}
