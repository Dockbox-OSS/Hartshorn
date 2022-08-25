package org.dockbox.hartshorn.commands.extension;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.AutoCreating;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.inject.Inject;

@AutoCreating
public class CommandExtensionContext extends DefaultApplicationAwareContext {

    private final Set<CommandExecutorExtension> extensions = ConcurrentHashMap.newKeySet();

    @Inject
    public CommandExtensionContext(final ApplicationContext applicationContext) {
        super(applicationContext);
    }

    public boolean add(final CommandExecutorExtension extension) {
        return this.extensions.add(extension);
    }

    public boolean remove(final CommandExecutorExtension extension) {
        return this.extensions.remove(extension);
    }

    public Set<CommandExecutorExtension> extensions() {
        return Set.copyOf(this.extensions);
    }
}
