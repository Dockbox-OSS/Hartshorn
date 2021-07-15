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

package org.dockbox.hartshorn.di.services;

import org.dockbox.hartshorn.di.annotations.activate.UseServiceProvision;
import org.dockbox.hartshorn.di.annotations.inject.Bound;
import org.dockbox.hartshorn.di.annotations.inject.Provider;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.inject.KeyBinding;
import org.dockbox.hartshorn.di.inject.ProviderContext;
import org.dockbox.hartshorn.di.inject.wired.ProvisionBoundContext;
import org.dockbox.hartshorn.di.inject.wired.BoundContext;
import org.dockbox.hartshorn.util.Reflect;

import java.lang.reflect.Method;
import java.util.Collection;

import javax.inject.Singleton;

public final class ProviderServiceProcessor implements ServiceProcessor<UseServiceProvision> {

    @Override
    public boolean preconditions(Class<?> type) {
        return !Reflect.methods(type, Provider.class).isEmpty();
    }

    @Override
    public <T> void process(ApplicationContext context, Class<T> type) {
        Collection<Method> methods = Reflect.methods(type, Provider.class);
        for (Method method : methods) {
            boolean singleton = Reflect.annotation(method, Singleton.class).present();
            Provider annotation = Reflect.annotation(method, Provider.class).get();

            if (Reflect.annotation(method, Bound.class).present()) {
                if (singleton) throw new IllegalArgumentException("Cannot provide manually bound singleton provider " + method.getReturnType() + " at " + method.getName());
                else {
                    BoundContext<?, ?> boundContext = new ProvisionBoundContext<>(method.getReturnType(), method, annotation.value());
                    context.add(boundContext);
                }
            }
            else {
                KeyBinding<?> key = "".equals(annotation.value())
                        ? KeyBinding.of(method.getReturnType())
                        : KeyBinding.of(method.getReturnType(), Bindings.named(annotation.value()));

                ProviderContext<?, ?> providerContext = new ProviderContext<>(key, singleton, () -> context.invoke(method));
                context.add(providerContext);
            }
        }
    }

    @Override
    public Class<UseServiceProvision> activator() {
        return UseServiceProvision.class;
    }
}
