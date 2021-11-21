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
import org.dockbox.hartshorn.core.annotations.activate.UseServiceProvision;
import org.dockbox.hartshorn.core.annotations.inject.Provider;
import org.dockbox.hartshorn.core.annotations.service.AutomaticActivation;
import org.dockbox.hartshorn.core.binding.Bindings;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.inject.ProviderContext;

import java.util.List;

@AutomaticActivation
public final class ProviderServiceProcessor implements ServiceProcessor<UseServiceProvision> {

    @Override
    public boolean preconditions(final ApplicationContext context, final TypeContext<?> type) {
        return !type.methods(Provider.class).isEmpty();
    }

    @Override
    public <T> void process(final ApplicationContext context, final TypeContext<T> type) {
        final List<MethodContext<?, T>> methods = type.methods(Provider.class);
        context.log().debug("Found " + methods.size() + " providers in " + type.name());
        for (final MethodContext<?, T> method : methods) {
            final boolean singleton = context.meta().singleton(method);
            final Provider annotation = method.annotation(Provider.class).get();

            final Key<?> key = "".equals(annotation.value())
                    ? Key.of(method.returnType())
                    : Key.of(method.returnType(), Bindings.named(annotation.value()));

            final ProviderContext<?> providerContext = new ProviderContext<>(((Key<Object>) key), singleton, annotation.priority(), () -> method.invoke(context).rethrowUnchecked().orNull());
            context.add(providerContext);
        }
    }

    @Override
    public Class<UseServiceProvision> activator() {
        return UseServiceProvision.class;
    }
}
