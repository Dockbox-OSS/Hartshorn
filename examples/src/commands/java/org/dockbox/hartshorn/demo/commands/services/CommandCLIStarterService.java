/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.demo.commands.services;

import org.dockbox.hartshorn.commands.CommandCLI;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
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
