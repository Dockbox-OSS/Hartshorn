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

package org.dockbox.hartshorn.commands.events;

import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.events.AbstractTargetEvent;
import org.dockbox.hartshorn.events.parents.Cancellable;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * The common parent for command-related events.
 */
public abstract class CommandEvent extends AbstractTargetEvent {

    @Getter private final CommandContext commandContext;

    protected CommandEvent(final CommandSource source, final CommandContext commandContext) {
        super(source);
        this.commandContext = commandContext;
    }

    /**
     * The event fired before a command is executed.
     */
    @Getter
    @Setter
    public static class Before extends CommandEvent implements Cancellable {

        private boolean cancelled;

        public Before(final CommandSource source, final CommandContext context) {
            super(source, context);
        }

        @Override
        public @NotNull Before post() {
            return (Before) super.post();
        }

        @Override
        public Before with(ApplicationContext context) {
            return (Before) super.with(context);
        }
    }

    /**
     * The event fired after a command is executed.
     */
    public static class After extends CommandEvent {
        public After(final CommandSource source, final CommandContext context) {
            super(source, context);
        }
    }
}
