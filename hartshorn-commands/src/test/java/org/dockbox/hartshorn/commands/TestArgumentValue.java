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

package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.commands.context.ArgumentConverter;
import org.dockbox.hartshorn.commands.values.AbstractArgumentElement;
import org.dockbox.hartshorn.commands.values.AbstractArgumentValue;

public class TestArgumentValue extends AbstractArgumentValue<String> {

    public TestArgumentValue(String permission, String key, String type) {
        super(permission, key, type);
    }

    @Override
    protected String parseValue(ArgumentConverter<?> converter, String key, String type) {
        return null;
    }

    @Override
    protected <E extends Enum<E>> void setEnumType(Class<E> enumType, String key) {
    }

    @Override
    public AbstractArgumentElement<String> getElement() {
        return null;
    }
}