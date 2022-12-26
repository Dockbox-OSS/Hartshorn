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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.beans.BeanContext;
import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.context.ContextKey;
import org.dockbox.hartshorn.inject.Enable;
import org.dockbox.hartshorn.inject.Populate;
import org.dockbox.hartshorn.inject.Required;
import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Collection;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.inject.Named;

public class ContextualComponentPopulator implements ComponentPopulator, ContextCarrier {

    private final ApplicationContext applicationContext;

    public ContextualComponentPopulator(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <T> T populate(final T instance) {
        if (null != instance) {
            T modifiableInstance = instance;
            if (this.applicationContext().environment().isProxy(instance)) {
                modifiableInstance = this.applicationContext().environment()
                        .manager(instance)
                        .flatMap(ProxyManager::delegate)
                        .orElse(modifiableInstance);
            }
            final TypeView<T> typeView = this.applicationContext.environment().introspect(modifiableInstance);
            if (Boolean.TRUE.equals(typeView.annotations().get(Populate.class).map(Populate::fields).orElse(true)))
                this.populateFields(typeView, modifiableInstance);

            if (Boolean.TRUE.equals(typeView.annotations().get(Populate.class).map(Populate::executables).orElse(true)))
                this.populateMethods(typeView, modifiableInstance);
        }
        return instance;
    }

    private <T> void populateMethods(final TypeView<T> type, final T instance) {
        for (final MethodView<T, ?> method : type.methods().annotatedWith(Inject.class)) {
            method.invokeWithContext(instance).rethrowUnchecked();
        }
    }

    private <T> void populateFields(final TypeView<T> type, final T instance) {
        for (final FieldView<T, ?> field : type.fields().annotatedWith(Inject.class)) {
            if (field.type().isChildOf(Collection.class))
                this.populateBeanCollectionField(type, instance, field);
            else this.populateObjectField(type, instance, field);
        }
        for (final FieldView<T, ?> field : type.fields().annotatedWith(org.dockbox.hartshorn.inject.Context.class)) {
            this.populateContextField(field, instance);
        }
    }

    private <T> void populateObjectField(final TypeView<T> type, final T instance, final FieldView<T, ?> field) {
        ComponentKey<?> fieldKey = ComponentKey.of(field.type().type());
        if (field.annotations().has(Named.class)) fieldKey = fieldKey.mut().name(field.annotations().get(Named.class).get()).build();

        final Option<Enable> enableAnnotation = field.annotations().get(Enable.class);
        final boolean enable = !enableAnnotation.present() || enableAnnotation.get().value();

        final ComponentKey<?> componentKey = fieldKey.mut().enable(enable).build();
        final Object fieldInstance = this.applicationContext().get(componentKey);

        final boolean required = Boolean.TRUE.equals(field.annotations().get(Required.class)
                .map(Required::value)
                .orElse(false));

        if (required && fieldInstance == null) throw new ComponentRequiredException("Field " + field.name() + " in " + type.qualifiedName() + " is required");

        this.applicationContext().log().debug("Injecting object of type {} into field {}", field.type().name(), field.qualifiedName());
        field.set(instance, fieldInstance);
    }

    private <T> void populateBeanCollectionField(final TypeView<T> type, final T instance, final FieldView<T, ?> field) {
        final Option<TypeView<?>> beanType = field.genericType().typeParameters().at(0);
        if (beanType.absent()) {
            throw new IllegalStateException("Unable to determine bean type for field " + field.name() + " in " + type.name());
        }
        ComponentKey<?> beanKey = ComponentKey.of(beanType.get().type());
        if (field.annotations().has(Named.class)) beanKey = beanKey.mut().name(field.annotations().get(Named.class).get()).build();

        final BeanContext beanContext = this.applicationContext().first(BeanContext.CONTEXT_KEY).get();
        final List<?> beans = beanContext.provider().all(beanKey);
        final Option<?> initialValue = field.get(instance);
        final Collection<?> transform = CollectionUtilities.transform(beans,
                TypeUtils.adjustWildcards(initialValue.orNull(), Collection.class),
                TypeUtils.adjustWildcards(field.type(), TypeView.class)
        );

        this.applicationContext().log().debug("Injecting bean collection of type {} into field {}", field.type().name(), field.qualifiedName());
        field.set(instance, transform);
    }

    protected <T> void populateContextField(final FieldView<T, ?> field, final T instance) {
        final TypeView<?> type = field.type();
        final org.dockbox.hartshorn.inject.Context annotation = field.annotations().get(org.dockbox.hartshorn.inject.Context.class).get();

        if (!type.isChildOf(Context.class)) {
            throw new IllegalStateException("Field " + field.name() + " in " + field.declaredBy().name() + " is annotated with @Context but is not a Context");
        }
        ContextKey<? extends Context> contextKey = ContextKey.of((TypeView<? extends Context>) type);
        if (StringUtilities.notEmpty(annotation.value())) {
            contextKey = contextKey.mutable().name(annotation.value()).build();
        }
        final Option<? extends Context> context = this.applicationContext().first(contextKey);

        final boolean required = Boolean.TRUE.equals(field.annotations().get(Required.class)
                .map(Required::value)
                .orElse(false));
        if (required && context.absent()) throw new ComponentRequiredException("Context field " + field.name() + " in " + type.qualifiedName() + " is required");

        this.applicationContext().log().debug("Injecting context of type {} into field {}", type, field.name());
        field.set(instance, context.orNull());
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }
}
