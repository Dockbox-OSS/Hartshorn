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

package org.dockbox.selene.core.impl.command;

import org.dockbox.selene.core.impl.command.convert.ArgumentConverter;

public abstract class AbstractArgumentValue<T> {

    protected T element;
    protected String permission;

    public AbstractArgumentValue(ArgumentConverter<?> argument, String permission, String key, String type) {
        this.element = this.parseArgument(argument, key, type);
        this.permission = permission;
    }

    protected abstract T parseArgument(ArgumentConverter<?> argument, String key, String type);
    public abstract T getArgument();

    public T getElement() {
        return this.element;
    }

    public String getPermission() {
        return this.permission;
    }

    public void setElement(T element) {
        this.element = element;
    }
}
