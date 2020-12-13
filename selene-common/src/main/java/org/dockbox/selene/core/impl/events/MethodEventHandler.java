/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.impl.events;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.util.eventbus.EventHandler;

import org.dockbox.selene.core.server.Selene;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Invokes a {@link Method} to dispatch an event. This type is a modified version of WorldEdit's
 * {@link com.sk89q.worldedit.util.eventbus.MethodEventHandler} so it handles exceptions appropriately according to
 * Selene conventions.
 */
public class MethodEventHandler extends EventHandler {

    private final Object object;
    private final Method method;

    /**
     * Create a new event handler.
     *
     * @param priority The priority at which the handler should be executed
     * @param object The listener object in which the method is invoked
     * @param method The method which is invoked
     */
    public MethodEventHandler(Priority priority, Object object, Method method) {
        super(priority);
        Preconditions.checkNotNull(method);
        this.object = object;
        this.method = method;
    }

    /**
     * Get the method.
     *
     * @return the method
     */
    public Method getMethod() {
        return this.method;
    }

    @Override
    public void dispatch(Object event) throws Exception {
        try {
            this.method.invoke(this.object, event);
        } catch (Exception e) {
            Selene.except("Could not invoke event listener", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (null == o || this.getClass() != o.getClass()) return false;

        MethodEventHandler that = (MethodEventHandler) o;

        return this.method.equals(that.method) && Objects.equals(this.object, that.object);
    }

    @Override
    public int hashCode() {
        int result = null != this.object ? this.object.hashCode() : 0;
        result = 31 * result + this.method.hashCode();
        return result;
    }
}
