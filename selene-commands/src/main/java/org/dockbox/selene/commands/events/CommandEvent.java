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

package org.dockbox.selene.commands.events;

import org.dockbox.selene.commands.context.CommandContext;
import org.dockbox.selene.commands.source.CommandSource;
import org.dockbox.selene.api.events.AbstractTargetCancellableEvent;

public abstract class CommandEvent extends AbstractTargetCancellableEvent {

    private final CommandContext context;

    protected CommandEvent(CommandSource source, CommandContext context) {
        super(source);
        this.context = context;
    }

    public CommandContext getContext() {
        return this.context;
    }

    public static class Before extends CommandEvent {
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
