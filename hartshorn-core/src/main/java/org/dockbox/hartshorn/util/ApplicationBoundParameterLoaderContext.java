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

package org.dockbox.hartshorn.util;

import java.util.List;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.context.ContextIdentity;
import org.dockbox.hartshorn.context.ContextView;
import org.dockbox.hartshorn.context.DefaultProvisionContext;
import org.dockbox.hartshorn.context.ProvisionContext;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderContext;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.option.Option;

public class ApplicationBoundParameterLoaderContext extends ParameterLoaderContext implements ContextCarrier, ProvisionContext {

    private final ApplicationContext applicationContext;
    private final ComponentProvider provider;
    private final Scope scope;

    private final Context context = new DefaultProvisionContext() {};

    public ApplicationBoundParameterLoaderContext(ExecutableElementView<?> executable, Object instance, ApplicationContext applicationContext) {
        this(executable, instance, applicationContext, applicationContext);
    }

    public ApplicationBoundParameterLoaderContext(ExecutableElementView<?> executable, Object instance, ApplicationContext applicationContext, Scope scope) {
        this(executable, instance, applicationContext, applicationContext, scope);
    }

    public ApplicationBoundParameterLoaderContext(ExecutableElementView<?> executable, Object instance, ApplicationContext applicationContext, ComponentProvider provider, Scope scope) {
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
        if (this.scope == this.provider) {
            return this.provider;
        }
        return new ComponentProvider() {
            @Override
            public <T> T get(ComponentKey<T> key, ComponentRequestContext requestContext) {
                ApplicationBoundParameterLoaderContext self = ApplicationBoundParameterLoaderContext.this;
                // Explicit scopes get priority, otherwise use our local scope
                if (key.scope() == Scope.DEFAULT_SCOPE) {
                    return self.provider.get(key.mutable().scope(self.scope).build(), requestContext);
                }
                return self.provider.get(key, requestContext);
            }
        };
    }

    @Override
    public <C extends ContextView> void addContext(C context) {
        this.context.addContext(context);
    }

    @Override
    public <C extends ContextView> void addContext(String name, C context) {
        this.context.addContext(name, context);
    }

    @Override
    public ContextView contextView() {
        return this.context.contextView();
    }

    @Override
    public List<ContextView> contexts() {
        return this.context.contexts();
    }

    @Override
    public <C extends ContextView> Option<C> firstContext(ContextIdentity<C> key) {
        return this.context.firstContext(key);
    }

    @Override
    public <C extends ContextView> List<C> contexts(ContextIdentity<C> key) {
        return this.context.contexts(key);
    }

    @Override
    public void copyContextTo(Context context) {
        this.context.copyContextTo(context);
    }
}
