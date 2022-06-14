package org.dockbox.hartshorn.commands;

import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.Provider;

import jakarta.inject.Singleton;

@Service
@RequiresActivator(UseCommands.class)
public class TestCommandProviders {

    @Provider(priority = 0)
    @Singleton
    public Class<? extends SystemSubject> systemSubject = JUnitSystemSubject.class;

}
