package org.dockbox.hartshorn.commands;

import java.io.InputStream;

/**
 * Represents a constant CLI which is capable of listening to command inputs. Commands may be entered through
 * any mean, like a command line, external event bus, or similar solutions.
public interface CommandCLI {
    void open();

    CommandCLI async(boolean async);
    CommandCLI input(InputStream stream);
    CommandCLI source(CommandSource source);
}
