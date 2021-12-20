/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.demo.commands.services;

import org.dockbox.hartshorn.commands.CommandCLI;
import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.boot.ApplicationState;
import org.dockbox.hartshorn.core.boot.ApplicationState.Started;
import org.dockbox.hartshorn.core.boot.HartshornApplication;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.events.EngineChangedState;
import org.dockbox.hartshorn.events.annotations.Listener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class CommandCLIStarterService {

    /**
     * The method activated when the engine is done starting, this is done automatically when the application
     * was bootstrapped through {@link HartshornApplication}.
     *
     * <p>In this example we wish to use the {@link CommandCLI} to be able to the file {@code commands.txt} to
     * enter commands. This can be done by overriding the default {@link InputStream} of the {@link CommandCLI}.
     * In this case the default implementation is {@link org.dockbox.hartshorn.commands.cli.SimpleCommandCLI}, which
     * uses {@link System#in}.
     *
     * <p>Note the use of the generic type parameter {@link Started} in the event. This causes this method to
     * activate only when a {@link EngineChangedState} event is posted with this exact type parameter. When the
     * posted parameter is another sub-class of {@link ApplicationState} this method will not
     * activate. However, if the notation of this event changed to {@code EngineChangedState<?>} it would activate
     * with any type parameter, as long as the event itself is a {@link EngineChangedState}.
     */
    @Listener
    public void on(final EngineChangedState<Started> event) throws IOException {
        final Exceptional<Path> commands = event.applicationContext().resourceLocator().resource("commands.txt");
        if (commands.present()) {
            final InputStream inputStream = Files.newInputStream(commands.get());
            event.applicationContext().get(CommandCLI.class).input(inputStream).open();
        }
    }
}
