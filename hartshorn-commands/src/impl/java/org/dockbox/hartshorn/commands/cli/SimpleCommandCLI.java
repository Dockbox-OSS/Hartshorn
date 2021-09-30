package org.dockbox.hartshorn.commands.cli;

import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.api.task.ThreadUtils;
import org.dockbox.hartshorn.commands.CommandCLI;
import org.dockbox.hartshorn.commands.CommandGateway;
import org.dockbox.hartshorn.commands.CommandSource;
import org.dockbox.hartshorn.commands.SystemSubject;
import org.dockbox.hartshorn.commands.exceptions.ParsingException;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.context.ApplicationContext;

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
                InputStream input = this.input();
                Scanner scanner = new Scanner(input)
        ) {
            Runnable task = () -> {
                while (this.running()) {
                    final String next = scanner.nextLine();
                    try {
                        this.gateway.accept(this.source(), next);
                    }
                    catch (ParsingException e) {
                        Except.handle(e);
                    }
                }
            };

            if (this.async()) this.threads.performAsync(task);
            else task.run();
        }
        catch (IOException e) {
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
