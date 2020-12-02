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

import org.dockbox.selene.core.command.CommandRunner;
import org.dockbox.selene.core.i18n.permissions.AbstractPermission;
import org.dockbox.selene.core.impl.command.SimpleCommandBus;
import org.dockbox.selene.core.impl.command.context.SimpleCommandContext;
import org.dockbox.selene.core.command.source.CommandSource;
import org.dockbox.selene.test.commands.TestArgumentValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestCommandBus extends SimpleCommandBus<SimpleCommandContext, TestArgumentValue> {

    @Override
    public void registerCommandNoArgs(@NotNull String command, @NotNull AbstractPermission permissions, @NotNull CommandRunner runner) {
        throw new UnsupportedOperationException("CommandBus is not testable in common implementations, and should only be tested in the platform implementation");
    }

    @Override
    protected void registerChildCommand(@NotNull String command, @NotNull CommandRunner runner, @NotNull String usagePart, @NotNull AbstractPermission permissions) {
        throw new UnsupportedOperationException("CommandBus is not testable in common implementations, and should only be tested in the platform implementation");
    }

    @Override
    protected void registerSingleMethodCommand(@NotNull String command, @NotNull CommandRunner runner, @NotNull String usagePart, @NotNull AbstractPermission permissions) {
        throw new UnsupportedOperationException("CommandBus is not testable in common implementations, and should only be tested in the platform implementation");
    }

    @Override
    protected void registerParentCommand(@NotNull String command, @NotNull CommandRunner runner, @NotNull AbstractPermission permissions) {
        throw new UnsupportedOperationException("CommandBus is not testable in common implementations, and should only be tested in the platform implementation");
    }

    @NotNull
    @Override
    protected SimpleCommandContext convertContext(SimpleCommandContext ctx, @NotNull CommandSource sender, @Nullable String command) {
        throw new UnsupportedOperationException("CommandBus is not testable in common implementations, and should only be tested in the platform implementation");
    }

    @Override
    protected TestArgumentValue getArgumentValue(@NotNull String type, @NotNull AbstractPermission permissions, @NotNull String key) {
        throw new UnsupportedOperationException("CommandBus is not testable in common implementations, and should only be tested in the platform implementation");
    }
}
