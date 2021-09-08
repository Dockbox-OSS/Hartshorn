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

package org.dockbox.hartshorn.di.binding;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.ContextCarrier;
import org.dockbox.hartshorn.di.Key;

import java.util.List;
import java.util.Map.Entry;

public interface BindingHierarchy<C> extends Iterable<Entry<Integer, Provider<C>>>, ContextCarrier {

    List<Provider<C>> providers();

    BindingHierarchy<C> add(Provider<C> provider);

    BindingHierarchy<C> add(int priority, Provider<C> provider);

    BindingHierarchy<C> addNext(Provider<C> provider);
    BindingHierarchy<C> merge(BindingHierarchy<C> hierarchy);

    int size();

    Exceptional<Provider<C>> get(int priority);

    Key<C> key();
}
