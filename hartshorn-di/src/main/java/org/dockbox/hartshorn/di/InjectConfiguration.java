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

package org.dockbox.hartshorn.di;

import org.dockbox.hartshorn.di.inject.Binder;
import org.dockbox.hartshorn.di.inject.DelegatedBinder;

public abstract class InjectConfiguration implements DelegatedBinder {

    private Binder binder;

    public final void setBinder(Binder binder) {
        this.binder = binder;
    }

    public abstract void collect();

    public final Binder getBinder() {
        if (this.binder == null) throw new IllegalStateException("No binder provided!");
        return this.binder;
    }
}
