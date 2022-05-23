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

package org.dockbox.hartshorn.component;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.inject.Enable;
import org.dockbox.hartshorn.inject.Populate;
import org.dockbox.hartshorn.inject.Required;
import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.function.CheckedFunction;
import org.dockbox.hartshorn.util.reflect.FieldContext;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import javax.inject.Inject;
import javax.inject.Named;

public class ContextualComponentPopulator implements ComponentPopulator, ContextCarrier {

    private final ApplicationContext applicationContext;

    public ContextualComponentPopulator(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <T> T populate(final T instance) {
        if (null != instance) {
            T modifiableInstance = instance;
            if (this.applicationContext().environment().manager().isProxy(instance)) {
                modifiableInstance = this.applicationContext().environment().manager()
                        .manager(instance)
                        .flatMap((CheckedFunction<ProxyManager<T>, @NonNull Result<T>>) ProxyManager::delegate)
                        .or(modifiableInstance);
            }
            final TypeContext<T> unproxied = TypeContext.unproxy(this.applicationContext(), modifiableInstance);
            if (unproxied.annotation(Populate.class).map(Populate::fields).or(true))
                modifiableInstance = this.populateFields(unproxied, modifiableInstance);

            if (unproxied.annotation(Populate.class).map(Populate::executables).or(true))
                modifiableInstance = this.populateMethods(unproxied, modifiableInstance);
        }
        return instance;
    }

    private <T> T populateMethods(final TypeContext<T> type, final T instance) {
        for (final MethodContext<?, T> method : type.methods(Inject.class)) {
            method.invoke(this.applicationContext(), instance).rethrowUnchecked();
        }
        return instance;
    }

    private <T> T populateFields(final TypeContext<T> type, final T instance) {
        for (final FieldContext<?> field : type.fields(Inject.class)) {
            Key<?> fieldKey = Key.of(field.type());
            if (field.annotation(Named.class).present()) fieldKey = Key.of(field.type(), field.annotation(Named.class).get());

            final Result<Enable> enableAnnotation = field.annotation(Enable.class);
            final boolean enable = !enableAnnotation.present() || enableAnnotation.get().value();

            final Object fieldInstance = this.applicationContext().get(fieldKey, enable);

            final boolean required = field.annotation(Required.class).map(Required::value).or(false);
            if (required && fieldInstance == null) return ExceptionHandler.unchecked(new ApplicationException("Field " + field.name() + " in " + type.qualifiedName() + " is required"));

            field.set(instance, fieldInstance);
        }
        for (final FieldContext<?> field : type.fields(org.dockbox.hartshorn.inject.Context.class)) {
            this.populateContextField(field, instance);
        }
        return instance;
    }

    protected void populateContextField(final FieldContext<?> field, final Object instance) {
        final TypeContext<?> type = field.type();
        final org.dockbox.hartshorn.inject.Context annotation = field.annotation(org.dockbox.hartshorn.inject.Context.class).get();

        final Result<Context> context;
        if ("".equals(annotation.value())) {
            context = this.applicationContext().first((Class<Context>) type.type());
        }
        else {
            context = this.applicationContext().first(annotation.value(), (Class<Context>) type.type());
        }

        final boolean required = field.annotation(Required.class).map(Required::value).or(false);
        if (required && context.absent()) ExceptionHandler.unchecked(new ApplicationException("Field " + field.name() + " in " + type.qualifiedName() + " is required"));

        field.set(instance, context.orNull());
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }
}
