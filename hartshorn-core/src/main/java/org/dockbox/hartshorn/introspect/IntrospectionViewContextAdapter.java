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

package org.dockbox.hartshorn.introspect;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.util.ApplicationBoundParameterLoaderContext;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.GenericTypeView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * TODO: #1060 Add documentation
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class IntrospectionViewContextAdapter extends DefaultContext implements ViewContextAdapter {

    private final ApplicationContext applicationContext;
    private final Scope scope;

    public IntrospectionViewContextAdapter(IntrospectionViewContextAdapter adapter, Scope scope) {
        this.applicationContext = adapter.applicationContext;
        this.scope = scope;
    }

    public IntrospectionViewContextAdapter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.scope = applicationContext;
    }

    private ComponentRequestContext componentRequestContext() {
        return this.firstContext(ComponentRequestContext.class)
                .orElseGet(ComponentRequestContext::createForComponent);
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public ViewContextAdapter scope(Scope scope) {
        return new IntrospectionViewContextAdapter(this, scope);
    }

    @Override
    public <T> Option<T> create(ConstructorView<T> constructor) throws Throwable {
        Object[] parameters = this.loadParameters(constructor);
        return constructor.create(parameters);
    }

    @Override
    public Object[] loadParameters(ExecutableElementView<?> element) {
        ExecutableElementContextParameterLoader parameterLoader = new ExecutableElementContextParameterLoader(
                this.applicationContext
        );

        InjectionPointParameterLoaderRule rule = new InjectionPointParameterLoaderRule(this.componentRequestContext());
        parameterLoader.add(rule);

        ApplicationBoundParameterLoaderContext loaderContext = new ApplicationBoundParameterLoaderContext(element, null, this.applicationContext(), this.scope);
        this.copyToContext(loaderContext);
        return parameterLoader.loadArguments(loaderContext).toArray();
    }

    @Override
    public <P, R> Option<R> invoke(MethodView<P, R> method) throws Throwable {
        if (method.modifiers().isStatic()) {
            return this.invokeStatic(method);
        }
        Object[] parameters = this.loadParameters(method);
        P instance = this.applicationContext().get(this.key(method.declaredBy().type()), this.componentRequestContext());
        return method.invoke(instance, parameters);
    }

    @Override
    public <P, R> Option<R> invokeStatic(MethodView<P, R> method) throws Throwable {
        if (!method.modifiers().isStatic()) {
            return this.invoke(method);
        }
        Object[] parameters = this.loadParameters(method);
        return method.invokeStatic(parameters);
    }

    @Override
    public <P, R> Option<R> load(FieldView<P, R> field) throws Throwable {
        P instance = this.applicationContext().get(this.key(field.declaredBy().type()), this.componentRequestContext());
        return field.get(instance);
    }

    @Override
    public <T> Option<T> load(GenericTypeView<T> element) throws Throwable {
        return switch(element) {
            case TypeView<?> typeView -> {
                ComponentKey<T> key = this.key(TypeUtils.adjustWildcards(typeView.type(), Class.class));
                yield Option.of(this.applicationContext().get(key, this.componentRequestContext()));
            }
            case FieldView<?, ?> fieldView -> this.load(TypeUtils.adjustWildcards(fieldView, FieldView.class));
            case MethodView<?, ?> methodView -> this.invoke(TypeUtils.adjustWildcards(methodView, MethodView.class));
            case ConstructorView<?> constructorView -> this.create(TypeUtils.adjustWildcards(constructorView, ConstructorView.class));
            case ParameterView<?> parameterView -> {
                ComponentKey<T> key = this.key(TypeUtils.adjustWildcards(parameterView.type().type(), Class.class));
                yield Option.of(this.applicationContext().get(key, this.componentRequestContext()));
            }
            default -> throw new IllegalArgumentException("Unsupported element type: " + element.getClass().getName());
        };
    }

    @Override
    public boolean isProxy(TypeView<?> type) {
        return !type.isWildcard() && this.applicationContext().environment().proxyOrchestrator().isProxy(type.type());
    }

    private <T> ComponentKey<T> key(Class<T> type) {
        return ComponentKey.builder(type).scope(this.scope).build();
    }
}
