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

package org.dockbox.selene.test.commands;

import org.dockbox.selene.core.impl.command.AbstractArgumentValue;
import org.dockbox.selene.core.impl.command.convert.ArgumentConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestArgumentValue extends AbstractArgumentValue<String> {

    public TestArgumentValue(@Nullable ArgumentConverter<?> argument, @Nullable String permission, @NotNull String key) {
        super(argument, permission, key);
    }

    @Override
    protected String parseArgument(@Nullable ArgumentConverter<?> argument, @Nullable String key) {
        throw new UnsupportedOperationException("ArgumentValue is not testable in common implementations, and should only be tested in the platform implementation");
    }

    @Override
    public String getArgument() {
        throw new UnsupportedOperationException("ArgumentValue is not testable in common implementations, and should only be tested in the platform implementation");
    }
}
