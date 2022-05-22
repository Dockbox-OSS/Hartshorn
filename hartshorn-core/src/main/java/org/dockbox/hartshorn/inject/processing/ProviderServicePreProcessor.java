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

package org.dockbox.hartshorn.inject.processing;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.Provider;
import org.dockbox.hartshorn.component.processing.ServicePreProcessor;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.inject.ProviderContext;
import org.dockbox.hartshorn.util.reflect.AnnotatedElementContext;
import org.dockbox.hartshorn.util.reflect.FieldContext;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.ObtainableElement;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.util.reflect.TypedElementContext;

import java.util.List;
import java.util.function.Function;

import javax.inject.Singleton;

public final class ProviderServicePreProcessor implements ServicePreProcessor<UseServiceProvision> {

    @Override
    public boolean preconditions(final ApplicationContext context, final Key<?> key) {
        return !(key.type().methods(Provider.class).isEmpty() && key.type().fields(Provider.class).isEmpty());
    }

    @Override
    public <T> void process(final ApplicationContext context, final Key<T> key) {
        final TypeContext<T> type = key.type();
        final List<MethodContext<?, T>> methods = type.methods(Provider.class);
        final List<FieldContext<?>> fields = type.fields(Provider.class);

        context.log().debug("Found " + (methods.size() + fields.size()) + " method providers in " + type.name());

        for (final MethodContext<?, T> method : methods)
            this.processContext(context, method);

        for (final FieldContext<?> field : fields)
            this.processContext(context, field);
    }

    private <E extends AnnotatedElementContext<?> & ObtainableElement<?> & TypedElementContext<?>> void processContext(final ApplicationContext context, final E element) {
        if (element.type().is(Class.class))
            this.processClassBinding(context, element.genericType(), (AnnotatedElementContext<?> & ObtainableElement<Class<Object>>) element);
        else if (element.type().is(TypeContext.class))
            this.processTypeBinding(context, element.genericType(), (AnnotatedElementContext<?> & ObtainableElement<TypeContext<Object>>) element);
        else
            this.processInstanceBinding(context, element, E::type);
    }

    private <T extends AnnotatedElementContext<?> & ObtainableElement<?>> void processInstanceBinding(final ApplicationContext context, final T element, final Function<T, TypeContext<?>> type) {
        final boolean singleton = context.meta().singleton(element);
        final Provider annotation = element.annotation(Provider.class).get();
        final Key<?> key = Key.of(type.apply(element), annotation.value());
        final ProviderContext<?> providerContext = new ProviderContext<>(((Key<Object>) key), singleton, annotation.priority(), () -> element.obtain(context).rethrowUnchecked().orNull(), annotation.lazy());

        context.add(providerContext);
    }

    private <R, C extends Class<R>, E extends AnnotatedElementContext<?> & ObtainableElement<C>> void processClassBinding(final ApplicationContext context, final TypeContext<?> generic, final E element) {
        final TypeContext<R> typeContext = (TypeContext<R>) generic.typeParameters().get(0);
        final Provider annotation = element.annotation(Provider.class).get();
        final Key<R> key = Key.of(typeContext, annotation.value());
        final boolean singleton = element.annotation(Singleton.class).present();

        if (context.meta().singleton(element)) {
            final C target = element.obtain(context).rethrowUnchecked().orNull();
            final ProviderContext<R> providerContext = new ProviderContext<>(key, singleton, annotation.priority(), () -> context.get(target), annotation.lazy());
            context.add(providerContext);
            return;
        }

        context.bind(key).to(element.obtain(context).rethrowUnchecked().get());
    }

    private <R, C extends TypeContext<R>, E extends AnnotatedElementContext<?> & ObtainableElement<C>> void processTypeBinding(final ApplicationContext context, final TypeContext<?> generic, final E element) {
        final TypeContext<R> typeContext = (TypeContext<R>) generic.typeParameters().get(0);
        final Provider annotation = element.annotation(Provider.class).get();
        final Key<R> key = Key.of(typeContext, annotation.value());
        final boolean singleton = element.annotation(Singleton.class).present();

        if (context.meta().singleton(element)) {
            final C target = element.obtain(context).rethrowUnchecked().orNull();
            final ProviderContext<R> providerContext = new ProviderContext<>(key, singleton, annotation.priority(), () -> context.get(target), annotation.lazy());
            context.add(providerContext);
            return;
        }

        context.bind(key).to(element.obtain(context).rethrowUnchecked().get().type());
    }

    @Override
    public Integer order() {
        return Integer.MIN_VALUE / 2;
    }
}
