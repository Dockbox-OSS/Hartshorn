/*
 * Copyright 2019-2024 the original author or authors.
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.Executors;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;

public class CommandListenerImpl implements CommandListener {

    private static final Logger LOG = LoggerFactory.getLogger(CommandListenerImpl.class);

    private final ApplicationContext context;
    private final CommandGateway gateway;

    @Inject
    public CommandListenerImpl(ApplicationContext applicationContext, CommandGateway gateway) {
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
    public CommandListenerImpl async(boolean async) {
        this.async = async;
        return this;
    }

    @Override
    public CommandListenerImpl input(InputStream input) {
        this.input = input;
        return this;
    }

    @Override
    public CommandListenerImpl source(CommandSource source) {
        this.source = source;
        return this;
    }

    @Override
    public void open() {
        Runnable task = this.createTask();

        if (this.async()) {
            LOG.debug("Performing startup task for command CLI asynchronously");
            Executors.newSingleThreadExecutor().submit(task);
        } else {
            LOG.debug("Performing startup task for command CLI on current thread");
            task.run();
        }
    }

    protected Runnable createTask() {
        return this::listenForInput;
    }

    private void listenForInput() {
        try (
                InputStream input = this.input();
                Scanner scanner = new Scanner(input)
        ) {
            LOG.debug("Starting command CLI input listener");
            while (this.running()) {
                String next = scanner.nextLine();
                try {
                    this.gateway.accept(this.source(), next);
                } catch (ParsingException e) {
                    this.context.handle(e);
                }
            }
        } catch (IOException e) {
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
