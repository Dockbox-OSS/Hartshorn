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

package org.dockbox.selene.di.context;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.di.InjectConfiguration;
import org.dockbox.selene.di.annotations.Named;
import org.dockbox.selene.di.inject.BeanContext;
import org.dockbox.selene.di.inject.Binder;
import org.dockbox.selene.di.inject.wired.WireContext;
import org.dockbox.selene.di.properties.InjectorProperty;

public interface ApplicationBinder extends Binder {

    void bind(InjectConfiguration configuration);
    void bind(String prefix);
    <T, I extends T> Exceptional<WireContext<T, I>> firstWire(Class<T> contract, InjectorProperty<Named> property);
    <T> T populate(T type);

    void add(WireContext<?, ?> context);
    void add(BeanContext<?, ?> context);

}
