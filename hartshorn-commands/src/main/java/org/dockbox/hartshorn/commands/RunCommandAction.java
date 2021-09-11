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

import org.dockbox.hartshorn.di.annotations.inject.Bound;
import org.dockbox.hartshorn.i18n.text.actions.ClickAction;
import org.dockbox.hartshorn.i18n.text.actions.CommandAction;

/**
 * Utility class to create a {@link RunCommand} action.
 */
public final class RunCommandAction {

    private RunCommandAction() {
    }

    /**
     * Creates a new {@link RunCommand} action for the given <code>command</code>
     *
     * @param command
     *         The command to perform when the action is run
     *
     * @return The new action
     */
    public static RunCommand runCommand(String command) {
        return new RunCommand(command);
    }

    /**
     * A specialized {@link ClickAction} capable
     * of performing a given command. Requires the <code>result</code> to be provided when
     * created through a {@link org.dockbox.hartshorn.di.binding.BoundFactoryProvider}.
     */
    public static final class RunCommand extends CommandAction<String> {

        @Bound
        private RunCommand(String result) {
            super(result);
        }
    }
}
