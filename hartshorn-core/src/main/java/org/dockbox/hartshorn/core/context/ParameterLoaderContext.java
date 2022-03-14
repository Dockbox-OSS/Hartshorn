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

package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.context.element.ExecutableElementContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;

public class ParameterLoaderContext extends DefaultContext implements ContextCarrier {

    private final ExecutableElementContext<?, ?> executable;
    private final TypeContext<?> type;
    private final Object instance;
    private final ApplicationContext applicationContext;
    private final ComponentProvider provider;

    public ParameterLoaderContext(final ExecutableElementContext<?, ?> executable, final TypeContext<?> type, final Object instance, final ApplicationContext applicationContext) {
        this.executable = executable;
        this.type = type;
        this.instance = instance;
        this.applicationContext = applicationContext;
        this.provider = applicationContext;
    }

    public ParameterLoaderContext(final ExecutableElementContext<?, ?> executable, final TypeContext<?> type, final Object instance, final ApplicationContext applicationContext, final ComponentProvider provider) {
        this.executable = executable;
        this.type = type;
        this.instance = instance;
        this.applicationContext = applicationContext;
        this.provider = provider;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    public ExecutableElementContext<?, ?> executable() {
        return this.executable;
    }

    public TypeContext<?> type() {
        return this.type;
    }

    public Object instance() {
        return this.instance;
    }

    public ComponentProvider provider() {
        return this.provider;
    }
}
