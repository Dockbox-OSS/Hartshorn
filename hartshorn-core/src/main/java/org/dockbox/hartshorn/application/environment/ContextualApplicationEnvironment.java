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

package org.dockbox.hartshorn.application.environment;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.scan.PrefixContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionIntrospector;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.inject.Singleton;

public class ContextualApplicationEnvironment implements ApplicationEnvironment {

    private final PrefixContext prefixContext;
    private final ApplicationManager manager;
    private Introspector introspector;

    public ContextualApplicationEnvironment(final PrefixContext prefixContext, final ApplicationManager manager) {
        this.manager = manager;
        this.prefixContext = prefixContext;
        this.manager().log().debug("Created new application environment (isCI: %s, prefixCount: %d)".formatted(this.isCI(), this.prefixContext().prefixes().size()));
    }

    @Override
    public PrefixContext prefixContext() {
        return this.prefixContext;
    }

    @Override
    public Introspector introspector() {
        if (this.introspector == null) {
            this.introspector = new ReflectionIntrospector(this.manager().applicationContext());
        }
        return this.introspector;
    }

    @Override
    public ApplicationManager manager() {
        return this.manager;
    }

    @Override
    public boolean isCI() {
        return this.manager.isCI();
    }

    @Override
    public void prefix(final String prefix) {
        this.prefixContext.prefix(prefix);
    }

    @Override
    public <A extends Annotation> Collection<TypeView<?>> types(final Class<A> annotation) {
        return this.prefixContext.types(annotation);
    }

    @Override
    public <A extends Annotation> Collection<TypeView<?>> types(final String prefix, final Class<A> annotation, final boolean skipParents) {
        return this.prefixContext.types(prefix, annotation, skipParents);
    }

    @Override
    public <A extends Annotation> Collection<TypeView<?>> types(final Class<A> annotation, final boolean skipParents) {
        return this.prefixContext.types(annotation, skipParents);
    }

    @Override
    public <T> Collection<TypeView<? extends T>> children(final TypeView<T> parent) {
        return this.prefixContext.children(parent);
    }

    @Override
    public <T> Collection<TypeView<? extends T>> children(final Class<T> parent) {
        return this.prefixContext.children(parent);
    }

    @Override
    public List<Annotation> annotationsWith(final TypeView<?> type, final Class<? extends Annotation> annotation) {
        final Collection<Annotation> annotations = new ArrayList<>();
        for (final Annotation typeAnnotation : type.annotations().all()) {
            if (this.introspect(typeAnnotation.annotationType()).annotations().has(annotation)) {
                annotations.add(typeAnnotation);
            }
        }
        return List.copyOf(annotations);
    }

    @Override
    public List<Annotation> annotationsWith(final Class<?> type, final Class<? extends Annotation> annotation) {
        return this.annotationsWith(this.introspect(type), annotation);
    }

    @Override
    public boolean singleton(final Class<?> type) {
        final TypeView<?> typeView = this.introspect(type);
        return this.singleton(typeView);
    }

    @Override
    public boolean singleton(final TypeView<?> type) {
        final ComponentLocator componentLocator = this.applicationContext().get(ComponentLocator.class);
        return Boolean.TRUE.equals(componentLocator.container(type.type())
                .map(ComponentContainer::singleton)
                .orElse(() -> type.annotations().has(Singleton.class))
                .or(false));
    }

    @Override
    public <T> TypeView<T> introspect(final Class<T> type) {
        return this.introspector().introspect(type);
    }

    @Override
    public <T> TypeView<T> introspect(final T instance) {
        return this.introspector().introspect(instance);
    }

    @Override
    public TypeView<?> introspect(final Type type) {
        return this.introspector().introspect(type);
    }

    @Override
    public TypeView<?> introspect(final ParameterizedType type) {
        return this.introspector().introspect(type);
    }

    @Override
    public TypeView<?> introspect(final String type) {
        return this.introspector().introspect(type);
    }

    @Override
    public MethodView<?, ?> introspect(final Method method) {
        return this.introspector().introspect(method);
    }

    @Override
    public <T> ConstructorView<T> introspect(final Constructor<T> method) {
        return this.introspector().introspect(method);
    }

    @Override
    public FieldView<?, ?> introspect(final Field field) {
        return this.introspector().introspect(field);
    }

    @Override
    public ParameterView<?> introspect(final Parameter parameter) {
        return this.introspector().introspect(parameter);
    }

    @Override
    public ElementAnnotationsIntrospector introspect(final AnnotatedElement annotatedElement) {
        return this.introspector().introspect(annotatedElement);
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.manager.applicationContext();
    }
}
