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
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.GenericTypeView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Attempt;

import jakarta.inject.Inject;

public class IntrospectionViewContextAdapter implements ViewContextAdapter {

    private final ApplicationContext applicationContext;
    private final Scope scope;

    public IntrospectionViewContextAdapter(ApplicationContext applicationContext, Scope scope) {
        this.applicationContext = applicationContext;
        this.scope = scope;
    }

    @Inject
    public IntrospectionViewContextAdapter(ApplicationContext applicationContext) {
        this(applicationContext, applicationContext);
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public ViewContextAdapter scope(Scope scope) {
        return new IntrospectionViewContextAdapter(this.applicationContext, scope);
    }

    @Override
    public <T> Attempt<T, Throwable> create(ConstructorView<T> constructor) {
        Object[] parameters = this.loadParameters(constructor);
        return constructor.create(parameters);
    }

    @Override
    public Object[] loadParameters(ExecutableElementView<?> element) {
        ExecutableElementContextParameterLoader parameterLoader = new ExecutableElementContextParameterLoader();
        ApplicationBoundParameterLoaderContext loaderContext = new ApplicationBoundParameterLoaderContext(element, null, this.applicationContext(), this.scope);
        return parameterLoader.loadArguments(loaderContext).toArray();
    }

    @Override
    public <P, R> Attempt<R, Throwable> invoke(MethodView<P, R> method) {
        if (method.modifiers().isStatic()) {
            return this.invokeStatic(method);
        }
        Object[] parameters = this.loadParameters(method);
        P instance = this.applicationContext().get(this.key(method.declaredBy().type()));
        return method.invoke(instance, parameters);
    }

    @Override
    public <P, R> Attempt<R, Throwable> invokeStatic(MethodView<P, R> method) {
        if (!method.modifiers().isStatic()) {
            return this.invoke(method);
        }
        Object[] parameters = this.loadParameters(method);
        return method.invokeStatic(parameters);
    }

    @Override
    public <P, R> Attempt<R, Throwable> load(FieldView<P, R> field) {
        P instance = this.applicationContext().get(this.key(field.declaredBy().type()));
        return field.get(instance);
    }

    @Override
    public <T> Attempt<T, Throwable> load(GenericTypeView<T> element) {
        if (element instanceof TypeView<?> typeView) {
            ComponentKey<T> key = this.key(TypeUtils.adjustWildcards(typeView.type(), Class.class));
            return Attempt.of(this.applicationContext().get(key));
        }
        else if (element instanceof FieldView<?, ?> fieldView) {
            return this.load(TypeUtils.adjustWildcards(fieldView, FieldView.class));
        }
        else if (element instanceof MethodView<?, ?> methodView) {
            return this.invoke(TypeUtils.adjustWildcards(methodView, MethodView.class));
        }
        else if (element instanceof ConstructorView<?> constructorView) {
            return this.create(TypeUtils.adjustWildcards(constructorView, ConstructorView.class));
        }
        else if (element instanceof ParameterView<?> parameterView) {
            ComponentKey<T> key = this.key(TypeUtils.adjustWildcards(parameterView.type().type(), Class.class));
            return Attempt.of(this.applicationContext().get(key));
        }
        return Attempt.of(new IllegalArgumentException("Unsupported element type: " + element.getClass().getName()));
    }

    @Override
    public boolean isProxy(TypeView<?> type) {
        return !type.isWildcard() && this.applicationContext().environment().isProxy(type.type());
    }

    private <T> ComponentKey<T> key(Class<T> type) {
        return ComponentKey.builder(type).scope(this.scope).build();
    }
}
