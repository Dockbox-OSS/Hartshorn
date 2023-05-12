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

package org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.context.ContextIdentity;
import org.dockbox.hartshorn.context.DefaultProvisionContext;
import org.dockbox.hartshorn.context.ProvisionContext;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderContext;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;

public class ApplicationBoundParameterLoaderContext extends ParameterLoaderContext implements ContextCarrier, ProvisionContext {

    private final ApplicationContext applicationContext;
    private final ComponentProvider provider;
    private final Scope scope;

    private final Context context = new DefaultProvisionContext() {};

    public ApplicationBoundParameterLoaderContext(final ExecutableElementView<?> executable, final Object instance, final ApplicationContext applicationContext) {
        this(executable, instance, applicationContext, applicationContext);
    }

    public ApplicationBoundParameterLoaderContext(final ExecutableElementView<?> executable, final Object instance, final ApplicationContext applicationContext, final Scope scope) {
        this(executable, instance, applicationContext, applicationContext, scope);
    }

    public ApplicationBoundParameterLoaderContext(ExecutableElementView<?, ?> executable, Object instance, ApplicationContext applicationContext, ComponentProvider provider, Scope scope) {
        super(executable, instance);
        this.applicationContext = applicationContext;
        this.provider = provider;
        this.scope = scope;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    public ComponentProvider provider() {
        if (this.scope == this.provider) return this.provider;
        return new ComponentProvider() {
            @Override
            public <T> T get(final ComponentKey<T> key) {
                final ApplicationBoundParameterLoaderContext self = ApplicationBoundParameterLoaderContext.this;
                // Explicit scopes get priority, otherwise use our local scope
                if (key.scope() == Scope.DEFAULT_SCOPE) return self.provider.get(key.mutable().scope(self.scope).build());
                return self.provider.get(key);
            }
        };
    }

    @Override
    public <C extends Context> void add(C context) {
        this.context.add(context);
    }

    @Override
    public <C extends Context> void add(String name, C context) {
        this.context.add(name, context);
    }

    @Override
    public List<Context> all() {
        return this.context.all();
    }

    @Override
    public <C extends Context> Option<C> first(ContextIdentity<C> key) {
        return this.context.first(key);
    }

    @Override
    public <C extends Context> List<C> all(ContextIdentity<C> key) {
        return this.context.all(key);
    }
}
