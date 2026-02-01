/*
 * Copyright 2019-2026 the original author or authors.
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

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderRule;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.HashSet;
import java.util.Set;

/**
 * Basic implementation of {@link ComponentExecutableInvocationAdapter} using the
 * {@link InjectionCapableApplication} to load parameters.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class InjectorExecutableInvocationAdapter extends DefaultContext
    implements ComponentExecutableInvocationAdapter {

    private final ComponentRequestContext componentRequestContext;
    private final InjectionCapableApplication application;
    private final Scope scope;
    private final Set<ParameterLoaderRule<ApplicationBoundParameterLoaderContext>> additionalRules =
            new HashSet<>();

    public InjectorExecutableInvocationAdapter(
        InjectorExecutableInvocationAdapter adapter,
        ComponentRequestContext componentRequestContext,
        Scope scope
    ) {
        this.application = adapter.application;
        this.scope = scope;
        this.componentRequestContext = componentRequestContext;
    }

    public InjectorExecutableInvocationAdapter(InjectionCapableApplication applicationContext) {
        this.application = applicationContext;
        this.componentRequestContext = ComponentRequestContext.createForComponent();
        this.scope = null; // Global scope by default
    }

    /**
     * Adds a parameter loader rule to this invocation adapter. The rule will be used when loading
     * parameters for executable elements.
     *
     * @param rule the rule to add
     *
     * @return this adapter for chaining
     */
    public InjectorExecutableInvocationAdapter addParameterLoaderRule(
        ParameterLoaderRule<ApplicationBoundParameterLoaderContext> rule
    ) {
        this.additionalRules.add(rule);
        return this;
    }

    private ComponentRequestContext componentRequestContext() {
        return this.componentRequestContext;
    }

    @Override
    public InjectorExecutableInvocationAdapter scope(Scope scope) {
        return new InjectorExecutableInvocationAdapter(this, this.componentRequestContext, scope);
    }

    @Override
    public InjectorExecutableInvocationAdapter requestContext(
        ComponentRequestContext componentRequestContext
    ) {
        return new InjectorExecutableInvocationAdapter(this, componentRequestContext, this.scope);
    }

    @Override
    public <T> Option<T> create(ConstructorView<T> constructor) throws Throwable {
        Object[] parameters = this.loadParameters(constructor);
        return Option.of(constructor.create(parameters));
    }

    @Override
    public Object[] loadParameters(ExecutableElementView<?> element) {
        ExecutableElementContextParameterLoader parameterLoader =
            new ExecutableElementContextParameterLoader(
                this.application
            );
        ComponentRequestContext componentRequestContext = this.componentRequestContext();
        if (componentRequestContext.isForInjectionPoint()) {
            var rule = new InjectionPointParameterLoaderRule(componentRequestContext);
            parameterLoader.add(rule);
        }
        for (var rule : this.additionalRules) {
            parameterLoader.add(rule);
        }
        var loaderContext = new ApplicationBoundParameterLoaderContext(
                element,
                null,
                this.application,
                this.scope()
        );
        this.copyToContext(loaderContext);
        return parameterLoader.loadArguments(loaderContext).toArray();
    }

    @Override
    public <P, R> Option<R> invoke(MethodView<P, R> method, P instance) throws Throwable {
        if (method.modifiers().isStatic()) {
            return this.invokeStatic(method);
        }
        else {
            Object[] parameters = this.loadParameters(method);
            return method.invoke(instance, parameters);
        }
    }

    @Override
    public <P, R> Option<R> invokeStatic(MethodView<P, R> method) throws Throwable {
        if (!method.modifiers().isStatic()) {
            throw new IllegalArgumentException("Method must be static to invoke statically: "
                + method);
        }
        Object[] parameters = this.loadParameters(method);
        return method.invokeStatic(parameters);
    }

    private Scope scope() {
        return this.scope != null
            ? this.scope
            : this.application.defaultProvider().scope();
    }
}
