/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.application.context.ApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.Executors;

import jakarta.inject.Inject;

public class CommandListenerImpl implements CommandListener {

    private final ApplicationContext context;
    private final CommandGateway gateway;

    @Inject
    public CommandListenerImpl(final ApplicationContext applicationContext, final CommandGateway gateway) {
        this.context = applicationContext;
        this.gateway = gateway;
        this.source(SystemSubject.instance(applicationContext));
    }

    private boolean async;
    private InputStream input = System.in;
    private CommandSource source;

    public boolean async() {
        return this.async;
    }

    public InputStream input() {
        return this.input;
    }

    @Override
    public CommandListenerImpl async(final boolean async) {
        this.async = async;
        return this;
    }

    @Override
    public CommandListenerImpl input(final InputStream input) {
        this.input = input;
        return this;
    }

    @Override
    public CommandListenerImpl source(final CommandSource source) {
        this.source = source;
        return this;
    }

    @Override
    public void open() {
        final Runnable task = this.createTask();

        if (this.async()) {
            this.context.log().debug("Performing startup task for command CLI asynchronously");
            Executors.newSingleThreadExecutor().submit(task);
        } else {
            this.context.log().debug("Performing startup task for command CLI on current thread");
            task.run();
        }
    }

    protected Runnable createTask() {
        return this::listenForInput;
    }

    private void listenForInput() {
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
        return this.source;
    }
}
