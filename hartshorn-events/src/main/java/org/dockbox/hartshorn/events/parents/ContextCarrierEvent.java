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

package org.dockbox.hartshorn.events.parents;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.Context;
import org.dockbox.hartshorn.core.context.DefaultContext;
import org.dockbox.hartshorn.events.EventBus;

public abstract class ContextCarrierEvent extends DefaultContext implements Event {

    private ApplicationContext context;

    @Override
    public ApplicationContext applicationContext() {
        if (this.context == null) {
            throw new IllegalStateException("Event instance has not been enhanced with application context");
        }
        return this.context;
    }

    @Override
    public <C extends Context> void add(final C context) {
        if (context instanceof ApplicationContext applicationContext) {
            this.with(applicationContext);
        }
        super.add(context);
    }

    @Override
    public ContextCarrierEvent with(final ApplicationContext context) {
        if (context == null) {
            throw new IllegalArgumentException("Cannot enhance with non-existent application context");
        }
        this.context = context;
        return this;
    }

    @Override
    public @NonNull Event post() {
        this.applicationContext().get(EventBus.class).post(this);
        return this;
    }
}
