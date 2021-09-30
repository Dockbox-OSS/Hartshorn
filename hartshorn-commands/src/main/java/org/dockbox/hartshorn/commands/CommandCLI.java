package org.dockbox.hartshorn.commands;

import java.io.InputStream;

/**
 * Represents a constant CLI which is capable of listening to command inputs. Commands may be entered through
 * any mean, like a command line, external event bus, or similar solutions. Should be activated after the engine
 * started, typically this can be done by listening for {@link org.dockbox.hartshorn.boot.EngineChangedState} with
 * {@link org.dockbox.hartshorn.boot.ServerState.Started} as its parameter.
 *
 * <p>For example
 * <pre>{@code
 * @Listener
 * public void on(EngineChangedState<Started> event) {
 *      event.applicationContext().get(CommandCLI.class).open();
 * }
 * }</pre>
 */
public interface CommandCLI {
    void open();

    CommandCLI async(boolean async);
    CommandCLI input(InputStream stream);
    CommandCLI source(CommandSource source);
}
