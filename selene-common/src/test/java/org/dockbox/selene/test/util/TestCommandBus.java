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

package org.dockbox.selene.test.util;

import org.dockbox.selene.core.command.CommandRunnerFunction;
import org.dockbox.selene.core.i18n.permissions.AbstractPermission;
import org.dockbox.selene.core.impl.command.SimpleCommandBus;
import org.dockbox.selene.core.impl.command.context.SimpleCommandContext;
import org.dockbox.selene.core.objects.targets.CommandSource;
import org.dockbox.selene.test.commands.TestArgumentValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestCommandBus extends SimpleCommandBus<SimpleCommandContext, TestArgumentValue> {

    @Override
    public void registerCommandArgsAndOrChild(@NotNull String command, @NotNull AbstractPermission permissions, @NotNull CommandRunnerFunction runner) {

    }

    @NotNull
    @Override
    protected SimpleCommandContext convertContext(SimpleCommandContext ctx, @NotNull CommandSource sender, @Nullable String command) {
        return null;
    }

    @Override
    protected TestArgumentValue getArgumentValue(@NotNull String type, @NotNull AbstractPermission permissions, @NotNull String key) {
        return null;
    }

    @Override
    public void registerCommandNoArgs(@NotNull String command, @NotNull AbstractPermission permissions, @NotNull CommandRunnerFunction runner) {

    }
}
