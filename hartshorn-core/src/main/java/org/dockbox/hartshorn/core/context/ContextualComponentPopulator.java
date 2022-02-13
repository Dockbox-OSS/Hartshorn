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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.inject.Enable;
import org.dockbox.hartshorn.core.annotations.inject.Populate;
import org.dockbox.hartshorn.core.annotations.inject.Required;
import org.dockbox.hartshorn.core.boot.ExceptionHandler;
import org.dockbox.hartshorn.core.context.element.FieldContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.function.CheckedFunction;
import org.dockbox.hartshorn.core.proxy.ProxyManager;

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
                        .flatMap((CheckedFunction<ProxyManager<T>, @NonNull Exceptional<T>>) ProxyManager::delegate)
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

            final Exceptional<Enable> enableAnnotation = field.annotation(Enable.class);
            final boolean enable = !enableAnnotation.present() || enableAnnotation.get().value();

            final Object fieldInstance = this.applicationContext().get(fieldKey, enable);

            final boolean required = field.annotation(Required.class).map(Required::value).or(false);
            if (required && fieldInstance == null) return ExceptionHandler.unchecked(new ApplicationException("Field " + field.name() + " in " + type.qualifiedName() + " is required"));

            field.set(instance, fieldInstance);
        }
        for (final FieldContext<?> field : type.fields(org.dockbox.hartshorn.core.annotations.inject.Context.class)) {
            this.populateContextField(field, instance);
        }
        return instance;
    }

    protected void populateContextField(final FieldContext<?> field, final Object instance) {
        final TypeContext<?> type = field.type();
        final org.dockbox.hartshorn.core.annotations.inject.Context annotation = field.annotation(org.dockbox.hartshorn.core.annotations.inject.Context.class).get();

        final Exceptional<org.dockbox.hartshorn.core.context.Context> context;
        if ("".equals(annotation.value())) {
            context = this.applicationContext().first((Class<org.dockbox.hartshorn.core.context.Context>) type.type());
        }
        else {
            context = this.applicationContext().first(annotation.value(), (Class<org.dockbox.hartshorn.core.context.Context>) type.type());
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
