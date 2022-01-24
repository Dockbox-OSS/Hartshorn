/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
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
