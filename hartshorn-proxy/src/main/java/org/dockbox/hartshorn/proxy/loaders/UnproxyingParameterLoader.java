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

import org.dockbox.hartshorn.proxy.advice.intercept.ProxyAdvisorMethodInterceptor;
import org.dockbox.hartshorn.util.introspect.util.RuleBasedParameterLoader;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;

/**
 * A parameter loader that attempts to unproxy arguments for several common use cases. This loader is used by default
 * by the {@link ProxyAdvisorMethodInterceptor} and can be used as a reference for custom implementations.
 *
 * @since 0.4.12
 * @author Guus Lieben
 */
public class UnproxyingParameterLoader extends RuleBasedParameterLoader<ProxyParameterLoaderContext> {

    public UnproxyingParameterLoader() {
        this.add(new UnproxyParameterLoaderRule());
        this.add(new ObjectEqualsParameterLoaderRule());
    }

    @Override
    protected <T> T loadDefault(final ParameterView<T> parameter, final int index, final ProxyParameterLoaderContext context, final Object... args) {
        return (T) args[index];
    }
}
