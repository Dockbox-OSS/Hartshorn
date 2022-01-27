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

package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.activate.AutomaticActivation;
import org.dockbox.hartshorn.core.annotations.activate.UseServiceProvision;
import org.dockbox.hartshorn.core.annotations.inject.Provider;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.AnnotatedElementContext;
import org.dockbox.hartshorn.core.context.element.FieldContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.inject.ProviderContext;

import java.util.List;
import java.util.function.Function;

@AutomaticActivation
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

        for (final MethodContext<?, T> method : methods) {
            this.process(context, method, MethodContext::returnType, m -> m.invoke(context));
        }

        for (final FieldContext<?> field : fields) {
            this.process(context, field, FieldContext::type, f -> f.get(context.get(key)));
        }
    }

    private <T extends AnnotatedElementContext<?>> void process(final ApplicationContext context, final T element, final Function<T, TypeContext<?>> type, final Function<T, Exceptional<?>> getter) {
        final boolean singleton = context.meta().singleton(element);
        final Provider annotation = element.annotation(Provider.class).get();

        final Key<?> providerKey = "".equals(annotation.value())
                ? Key.of(type.apply(element).type())
                : Key.of(type.apply(element).type(), annotation.value());

        final ProviderContext<?> providerContext = new ProviderContext<>(((Key<Object>) providerKey), singleton, annotation.priority(), () -> getter.apply(element).rethrowUnchecked().orNull());
        context.add(providerContext);
    }

    @Override
    public Class<UseServiceProvision> activator() {
        return UseServiceProvision.class;
    }
}
