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
