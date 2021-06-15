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

package org.dockbox.hartshorn.test.util;

import org.dockbox.hartshorn.commands.DefaultCommandBus;
import org.dockbox.hartshorn.commands.registration.AbstractCommandContext;
import org.dockbox.hartshorn.commands.registration.ParentCommandContext;
import org.dockbox.hartshorn.commands.values.AbstractArgumentElement;
import org.dockbox.hartshorn.commands.values.ArgumentValue;

import java.util.List;

public class JUnitCommandBus extends DefaultCommandBus {
    @Override
    protected Object buildContextExecutor(AbstractCommandContext context, String alias) {
        return null;
    }

    @Override
    protected Object buildInheritedContextExecutor(ParentCommandContext context, String alias) {
        return null;
    }

    @Override
    protected void registerExecutor(Object executor, String alias) {

    }

    @Override
    protected Object tryConvertObject(Object obj) {
        return null;
    }

    @Override
    protected ArgumentValue<?> getArgumentValue(String type, String permission, String key) {
        return null;
    }

    @Override
    protected AbstractArgumentElement<?> wrapElements(List list) {
        return null;
    }
}
