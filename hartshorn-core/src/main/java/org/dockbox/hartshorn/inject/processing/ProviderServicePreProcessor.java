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
import org.dockbox.hartshorn.component.processing.ExitingComponentProcessor;
import org.dockbox.hartshorn.component.processing.Provider;
import org.dockbox.hartshorn.component.processing.ServicePreProcessor;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.reflect.AnnotatedElementContext;
import org.dockbox.hartshorn.util.reflect.FieldContext;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.ObtainableElement;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.util.reflect.TypedElementContext;

import java.util.List;

public final class ProviderServicePreProcessor implements ServicePreProcessor, ExitingComponentProcessor {

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

        final ProviderListContext providerContext = context.first(ProviderListContext.class).orNull();
        for (final MethodContext<?, T> method : methods) {
            this.register(providerContext, method);
        }
        for (final FieldContext<?> field : fields) {
            this.register(providerContext, field);
        }
    }

    private <E extends AnnotatedElementContext<?> & ObtainableElement<?> & TypedElementContext<?>> void register(final ProviderListContext context, final E element) {
        final Key<?> key = this.key(element);
        context.add(key, element);
    }

    @Override
    public void exit(final ApplicationContext context) {
        final BindingProcessor processor = new BindingProcessor();
        context.bind(BindingProcessor.class).singleton(processor);

        final ProviderListContext providerContext = context.first(ProviderListContext.class).orNull();
        try {
            processor.process(providerContext, context);
        } catch (final ApplicationException e) {
            context.handle(e);
        }
    }

    private <E extends AnnotatedElementContext<?> & ObtainableElement<?> & TypedElementContext<?>> Key<?> key(final E element) {
        final Provider annotation = element.annotation(Provider.class).get();
        if (element.type().is(Class.class) || element.type().is(TypeContext.class)) {
            final TypeContext<?> typeContext = element.genericType().typeParameters().get(0);
            return Key.of(typeContext, annotation.value());
        }
        else {
            return Key.of(element.type(), annotation.value());
        }
    }

    @Override
    public Integer order() {
        return Integer.MIN_VALUE / 2;
    }
}
