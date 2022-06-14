package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.commands.events.CommandEvent.Before;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.events.annotations.Listener;

@Service
@RequiresActivator(UseMethodCancelling.class)
public class MethodCancellingActivator {

    @Listener
    public void onBefore(final Before before) {
        before.cancelled(true);
    }
}
