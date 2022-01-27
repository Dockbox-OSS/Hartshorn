package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.core.annotations.inject.Provider;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;

@Service(activators = UseCommands.class)
public class CommandProviders {

    @Provider
    public CommandListener listener() {
        return new CommandListenerImpl();
    }
}
