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

package org.dockbox.hartshorn.commands.beta.impl;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.beta.api.CommandContainerContext;
import org.dockbox.hartshorn.commands.beta.api.CommandExecutor;
import org.dockbox.hartshorn.commands.beta.api.CommandExecutorContext;
import org.dockbox.hartshorn.di.context.DefaultContext;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

import lombok.Getter;

@Getter
public class MethodCommandExecutorContext extends DefaultContext implements CommandExecutorContext {

    private final Method method;
    private final Class<?> type;
    private final Command command;
    @Nullable
    private final Command parent;
    private final boolean isChild;

    public MethodCommandExecutorContext(Method method, Class<?> type) {
        if (!method.isAnnotationPresent(Command.class)) throw new IllegalArgumentException("Provided method is not a command handler");
        this.method = method;
        this.type = type;
        this.command = method.getAnnotation(Command.class);

        if (type.isAnnotationPresent(Command.class)) {
            this.parent = type.getAnnotation(Command.class);
            this.isChild = true;
        }
        else {
            this.parent = null;
            this.isChild = false;
        }

        this.add(new DecoratorCommandContainerContext(this.command));
    }

    @Override
    public CommandExecutor executor() {
        return (ctx) -> {
            // TODO: Method invoking
        };
    }

    @Override
    public boolean accepts(String command) {
        Command annotation = this.isChild ? this.parent : this.command;

        final String alias = command.split(" ")[0];
        if (!HartshornUtils.contains(annotation.value(), alias)) {
            return false;
        }

        final Exceptional<CommandContainerContext> container = this.first(CommandContainerContext.class);
        if (container.absent()) throw new IllegalStateException("Container context was lost!");
        final CommandContainerContext containerContext = container.get();

        return containerContext.matches(command);
    }
}
