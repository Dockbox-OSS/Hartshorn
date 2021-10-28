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

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.inject.Binder;
import org.dockbox.hartshorn.core.inject.DelegatedBinder;

import lombok.Setter;

public abstract class InjectConfiguration implements DelegatedBinder {

    @Setter private Binder binder;

    public abstract void collect(ApplicationContext context);

    public final Binder binder() {
        if (this.binder == null) throw new IllegalStateException("No binder provided!");
        return this.binder;
    }
}