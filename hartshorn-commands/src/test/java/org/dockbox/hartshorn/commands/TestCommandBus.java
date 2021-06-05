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

import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.api.i18n.entry.FakeResource;
import org.dockbox.hartshorn.commands.registration.AbstractRegistrationContext;
import org.dockbox.hartshorn.commands.registration.CommandInheritanceContext;
import org.dockbox.hartshorn.commands.values.AbstractArgumentElement;
import org.dockbox.hartshorn.commands.values.ArgumentValue;

import java.util.List;

public class TestCommandBus extends DefaultCommandBus<Void> {

    {
        super.resources = new CommandResources() {
            @Override
            public ResourceEntry getConfirmCommand() {
                return new FakeResource("");
            }

            @Override
            public ResourceEntry getConfirmCommandHover() {
                return new FakeResource("");
            }

            @Override
            public ResourceEntry getMissingArguments() {
                return new FakeResource("");
            }
        };
    }

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
