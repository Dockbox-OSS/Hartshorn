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

package org.dockbox.selene.commands;

import org.dockbox.selene.commands.registration.AbstractRegistrationContext;
import org.dockbox.selene.commands.registration.CommandInheritanceContext;
import org.dockbox.selene.commands.values.AbstractArgumentElement;
import org.dockbox.selene.commands.values.ArgumentValue;

import java.util.List;

public class TestCommandBus extends DefaultCommandBus<Void> {

    @Override
    protected Void buildContextExecutor(AbstractRegistrationContext context, String alias) {
        return null;
    }

    @Override
    protected Void buildInheritedContextExecutor(CommandInheritanceContext context, String alias) {
        return null;
    }

    @Override
    protected void registerExecutor(Void executor, String alias) {

    }

    @Override
    protected Object tryConvertObject(Object obj) {
        return null;
    }

    @Override
    protected ArgumentValue<?> getArgumentValue(String type, String permission, String key) {
        return new TestArgumentValue(permission, key, type);
    }

    @Override
    protected AbstractArgumentElement<?> wrapElements(List<AbstractArgumentElement<?>> elements) {
        return null;
    }

}
