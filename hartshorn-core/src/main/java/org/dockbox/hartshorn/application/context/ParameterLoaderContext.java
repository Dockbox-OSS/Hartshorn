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

package org.dockbox.hartshorn.application.context;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class ParameterLoaderContext extends DefaultContext implements ContextCarrier {

    private final ExecutableElementView<?> executable;
    private final TypeView<?> type;
    private final Object instance;
    private final ApplicationContext applicationContext;
    private final ComponentProvider provider;
    private final Scope scope;

    public ParameterLoaderContext(final ExecutableElementView<?> executable, final TypeView<?> type, final Object instance, final ApplicationContext applicationContext) {
        this(executable, type, instance, applicationContext, applicationContext);
    }

    public ParameterLoaderContext(final ExecutableElementView<?> executable, final TypeView<?> type, final Object instance, final ApplicationContext applicationContext, final Scope scope) {
        this(executable, type, instance, applicationContext, applicationContext, scope);
    }

    public ParameterLoaderContext(final ExecutableElementView<?> executable, final TypeView<?> type, final Object instance, final ApplicationContext applicationContext, final ComponentProvider provider, final Scope scope) {
        this.executable = executable;
        this.type = type;
        this.instance = instance;
        this.applicationContext = applicationContext;
        this.provider = provider;
        this.scope = scope;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    public ExecutableElementView<?> executable() {
        return this.executable;
    }

    public TypeView<?> type() {
        return this.type;
    }

    public Object instance() {
        return this.instance;
    }

    public ComponentProvider provider() {
        if (this.scope == this.provider) return this.provider;
        return new ComponentProvider() {
            @Override
            public <T> T get(final ComponentKey<T> key) {
                final ParameterLoaderContext self = ParameterLoaderContext.this;
                // Explicit scopes get priority, otherwise use our local scope
                if (key.scope() == Scope.DEFAULT_SCOPE) return self.provider.get(key.mutable().scope(self.scope).build());
                return self.provider.get(key);
            }
        };
    }
}
