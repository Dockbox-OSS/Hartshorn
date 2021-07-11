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

import org.dockbox.hartshorn.api.events.AbstractTargetEvent;
import org.dockbox.hartshorn.api.events.parents.Cancellable;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.source.CommandSource;

import lombok.Getter;

public abstract class CommandEvent extends AbstractTargetEvent {

    @Getter
    private final CommandContext context;

    protected CommandEvent(CommandSource source, CommandContext context) {
        super(source);
        this.context = context;
    }

    public static class Before extends CommandEvent implements Cancellable {

        private boolean isCancelled;

        @Override
        public boolean isCancelled() {
            return this.isCancelled;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.isCancelled = cancelled;
        }

        public Before(CommandSource source, CommandContext context) {
            super(source, context);
        }
    }

    public static class After extends CommandEvent {
        public After(CommandSource source, CommandContext context) {
            super(source, context);
        }
    }
}
