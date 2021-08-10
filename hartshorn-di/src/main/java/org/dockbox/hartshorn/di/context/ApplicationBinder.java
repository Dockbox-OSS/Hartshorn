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

package org.dockbox.hartshorn.di.context;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.InjectConfiguration;
import org.dockbox.hartshorn.di.Key;
import org.dockbox.hartshorn.di.annotations.inject.Named;
import org.dockbox.hartshorn.di.binding.BindingHierarchy;
import org.dockbox.hartshorn.di.inject.Binder;
import org.dockbox.hartshorn.di.inject.ProviderContext;
import org.dockbox.hartshorn.di.inject.wired.BoundContext;

import java.lang.reflect.Method;

public interface ApplicationBinder extends Binder {

    void bind(InjectConfiguration configuration);

    void bind(String prefix);

    <T, I extends T> Exceptional<BoundContext<T, I>> firstWire(Class<T> contract, Named property);

    <T> T populate(T type);

    void add(BoundContext<?, ?> context);

    void add(ProviderContext<?, ?> context);

    <T> T invoke(Method method);

    <T> T invoke(Method method, Object instance);

    <T> BindingHierarchy<T> hierarchy(Key<T> key);
}
