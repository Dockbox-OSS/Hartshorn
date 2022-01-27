/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.commands.cli;

import org.dockbox.hartshorn.commands.CommandCLI;
import org.dockbox.hartshorn.commands.CommandGateway;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.SystemSubject;
import org.dockbox.hartshorn.commands.exceptions.ParsingException;
import org.dockbox.hartshorn.core.annotations.inject.ComponentBinding;
import org.dockbox.hartshorn.core.context.ApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

@ComponentBinding(CommandCLI.class)
public class SimpleCommandCLI implements CommandCLI {

    @Inject
    private ApplicationContext context;
    @Inject
    private CommandGateway gateway;

    @Getter
    @Setter
    private boolean async;
    @Getter
    @Setter
    private InputStream input = System.in;
    @Setter
    private CommandSource source;

    @Override
    public void open() {
        final Runnable task = () -> {
            try (
                    final InputStream input = this.input();
                    final Scanner scanner = new Scanner(input)
            ) {
                this.context.log().debug("Starting command CLI input listener");
                while (this.running()) {
                    final String next = scanner.nextLine();
                    try {
                        this.gateway.accept(this.source(), next);
                    } catch (final ParsingException e) {
                        this.context.handle(e);
                    }
                }
            } catch (final IOException e) {
                this.context.handle(e);
            }
        };

        if (this.async()) {
            this.context.log().debug("Performing startup task for command CLI asynchronously");
            new Thread(task, "command_cli").start();
        } else {
            this.context.log().debug("Performing startup task for command CLI on current thread");
            task.run();
        }
    }

    /**
     Indicates whether the command input is still active. If this method returns {@code false} the CLI closes.

     @return Whether to keep the CLI alive.
     */
    protected boolean running() {
        return true;
    }

    /**
     Gets the {@link CommandSource} which is used to execute commands for the current CLI session.

     @return The source to execute commands.
     */
    protected CommandSource source() {
        return SystemSubject.instance(this.context);
    }
}
