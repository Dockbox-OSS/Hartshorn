/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.inject.introspect;

import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.context.ContextIdentity;
import org.dockbox.hartshorn.context.ContextView;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.DefaultFallbackCompatibleContext;
import org.dockbox.hartshorn.inject.FallbackCompatibleContext;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.provider.ComponentProvider;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderContext;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;

/**
 * A parameter loader context that is aware of the application it is bound to, and in which scope it
 * operates.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ApplicationBoundParameterLoaderContext extends ParameterLoaderContext
    implements FallbackCompatibleContext {

    private final InjectionCapableApplication application;
    private final ComponentProvider provider;
    private final Scope scope;

    private final Context context = new DefaultFallbackCompatibleContext() {
    };

    public ApplicationBoundParameterLoaderContext(
        ExecutableElementView<?> executable,
        Object instance,
        InjectionCapableApplication application
    ) {
        this(executable, instance, application, application.defaultProvider().scope());
    }

    public ApplicationBoundParameterLoaderContext(
        ExecutableElementView<?> executable,
        Object instance,
        InjectionCapableApplication application,
        Scope scope
    ) {
        this(executable, instance, application, application.defaultProvider(), scope);
    }

    public ApplicationBoundParameterLoaderContext(
        ExecutableElementView<?> executable,
        Object instance,
        InjectionCapableApplication application,
        ComponentProvider provider,
        Scope scope
    ) {
        super(executable, instance);
        this.application = application;
        this.provider = provider;
        this.scope = scope;
    }

    /**
     * Returns the application this context is bound to.
     *
     * @return the application
     */
    public InjectionCapableApplication application() {
        return this.application;
    }

    /**
     * Returns the scope in which this context operates.
     *
     * @return the scope
     */
    public Scope scope() {
        return this.scope;
    }

    /**
     * Returns the provider that is used to resolve components in this context. This is the
     * scope-specific provider, which may differ from the application's default provider if a
     * specific non-application scope is set.
     *
     * @return the component provider for this context
     */
    public ComponentProvider provider() {
        if (this.scope == this.provider) {
            return this.provider;
        }
        return new ComponentProvider() {
            @Override
            public <T> T get(ComponentKey<T> key, ComponentRequestContext requestContext) {
                ApplicationBoundParameterLoaderContext self =
                    ApplicationBoundParameterLoaderContext.this;
                // Explicit scopes get priority, otherwise use our local scope
                if (key.scope().contains(self.application.defaultProvider().scope())) {
                    return self.provider.get(key.mutable().scope(self.scope).build(),
                        requestContext);
                }
                return self.provider.get(key, requestContext);
            }

            @Override
            public Scope scope() {
                return ApplicationBoundParameterLoaderContext.this.scope;
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
    public void copyToContext(Context context) {
        this.context.copyToContext(context);
    }
}
