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

package org.dockbox.hartshorn.commands.cli;

import org.dockbox.hartshorn.core.exceptions.Except;
import org.dockbox.hartshorn.core.task.ThreadUtils;
import org.dockbox.hartshorn.commands.CommandCLI;
import org.dockbox.hartshorn.commands.CommandGateway;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.SystemSubject;
import org.dockbox.hartshorn.commands.exceptions.ParsingException;
import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.context.ApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

@Binds(CommandCLI.class)
public class SimpleCommandCLI implements CommandCLI {

    @Inject private ApplicationContext context;
    @Inject private CommandGateway gateway;
    @Inject private ThreadUtils threads;

    @Getter @Setter
    private boolean async = false;
    @Getter @Setter
    private InputStream input = System.in;
    @Setter
    private CommandSource source;

    @Override
    public void open() {
        try (
                final InputStream input = this.input();
                final Scanner scanner = new Scanner(input)
        ) {
            final Runnable task = () -> {
                this.context.log().debug("Starting command CLI input listener");
                while (this.running()) {
                    final String next = scanner.nextLine();
                    try {
                        this.gateway.accept(this.source(), next);
                    }
                    catch (final ParsingException e) {
                        Except.handle(e);
                    }
                }
            };

            if (this.async()) {
                this.context.log().debug("Performing startup task for command CLI asynchronously");
                this.threads.performAsync(task);
            }
            else {
                this.context.log().debug("Performing startup task for command CLI on current thread");
                task.run();
            }
        }
        catch (final IOException e) {
            Except.handle(e);
        }
    }

    /**
     * Indicates whether the command input is still active. If this method returns {@code false} the CLI closes.
     * @return Whether to keep the CLI alive.
     */
    protected boolean running() {
        return true;
    }

    /**
     * Gets the {@link CommandSource} which is used to execute commands for the current CLI session.
     * @return The source to execute commands.
     */
    protected CommandSource source() {
        return SystemSubject.instance(this.context);
    }
}
