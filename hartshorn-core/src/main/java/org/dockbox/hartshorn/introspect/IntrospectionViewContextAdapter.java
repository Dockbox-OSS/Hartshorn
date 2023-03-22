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

package org.dockbox.hartshorn.introspect;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.util.ApplicationBoundParameterLoaderContext;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Attempt;

import jakarta.inject.Inject;

public class IntrospectionViewContextAdapter implements ViewContextAdapter {

    private final ApplicationContext applicationContext;
    private final Scope scope;

    public IntrospectionViewContextAdapter(final ApplicationContext applicationContext, final Scope scope) {
        this.applicationContext = applicationContext;
        this.scope = scope;
    }

    @Inject
    public IntrospectionViewContextAdapter(final ApplicationContext applicationContext) {
        this(applicationContext, applicationContext);
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public ViewContextAdapter scope(final Scope scope) {
        return new IntrospectionViewContextAdapter(this.applicationContext, scope);
    }

    @Override
    public <T> Attempt<T, Throwable> create(final ConstructorView<T> constructor) {
        final Object[] parameters = this.loadParameters(constructor);
        return constructor.create(parameters);
    }

    @Override
    public Object[] loadParameters(final ExecutableElementView<?, ?> element) {
        final ExecutableElementContextParameterLoader parameterLoader = new ExecutableElementContextParameterLoader();
        final ApplicationBoundParameterLoaderContext loaderContext = new ApplicationBoundParameterLoaderContext(element, null, this.applicationContext(), this.scope);
        return parameterLoader.loadArguments(loaderContext).toArray();
    }

    @Override
    public <P, R> Attempt<R, Throwable> invoke(final MethodView<P, R> method) {
        if (method.isStatic()) {
            return this.invokeStatic(method);
        }
        final Object[] parameters = this.loadParameters(method);
        final P instance = this.applicationContext().get(this.key(method.declaredBy().type()));
        return method.invoke(instance, parameters);
    }

    @Override
    public <P, R> Attempt<R, Throwable> invokeStatic(final MethodView<P, R> method) {
        if (!method.isStatic()) {
            return this.invoke(method);
        }
        final Object[] parameters = this.loadParameters(method);
        return method.invokeStatic(parameters);
    }

    @Override
    public <P, R> Attempt<R, Throwable> load(final FieldView<P, R> field) {
        final P instance = this.applicationContext().get(this.key(field.declaredBy().type()));
        return field.get(instance);
    }

    @Override
    public <T> Attempt<T, Throwable> load(final AnnotatedElementView<T> element) {
        if (element instanceof TypeView<T> typeView) {
            return Attempt.of(this.applicationContext().get(this.key(typeView.type())));
        }
        else if (element instanceof FieldView<?, T> fieldView) {
            return this.load(fieldView);
        }
        else if (element instanceof MethodView<?, T> methodView) {
            return this.invoke(methodView);
        }
        else if (element instanceof ConstructorView<T> constructorView) {
            return this.create(constructorView);
        }
        else if (element instanceof ParameterView<T> parameterView) {
            final ComponentKey<T> key = this.key(parameterView.type().type());
            return Attempt.of(this.applicationContext().get(key));
        }
        return Attempt.of(new IllegalArgumentException("Unsupported element type: " + element.getClass().getName()));
    }

    @Override
    public boolean isProxy(final TypeView<?> type) {
        return !type.isWildcard() && this.applicationContext().environment().isProxy(type.type());
    }

    private <T> ComponentKey<T> key(final Class<T> type) {
        return ComponentKey.builder(type).scope(this.scope).build();
    }
}
