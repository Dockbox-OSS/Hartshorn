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

package org.dockbox.hartshorn.demo.persistence.services;

import org.dockbox.hartshorn.demo.persistence.domain.User;
import org.dockbox.hartshorn.demo.persistence.events.UserCreatedEvent;

import org.dockbox.hartshorn.commands.CommandCLI;
import org.dockbox.hartshorn.core.annotations.service.Service;
import org.dockbox.hartshorn.core.boot.ApplicationState;
import org.dockbox.hartshorn.core.boot.ApplicationState.Started;
import org.dockbox.hartshorn.core.boot.HartshornApplication;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.events.EngineChangedState;
import org.dockbox.hartshorn.events.annotations.Listener;

/**
 * A simple service capable of listening to events. Any type annotated with {@link Service} (or an
 * extension of it) is automatically subscribed if there are methods annotated with {@link Listener}.
 */
@Service
public class EventListenerService {

    /**
     * The method activated when a new user is created and a {@link UserCreatedEvent} is fired. In this
     * example this is done through {@link UserRepository}.
     */
    @Listener
    public void on(final UserCreatedEvent event) {
        final ApplicationContext context = event.applicationContext();
        final User user = event.user();
        context.log().info("Created a new user with name %s and age %s and id %s".formatted(user.name(), user.age(), user.id()));
    }

    /**
     * The method activated when the engine is done starting, this is done automatically when the application
     * was bootstrapped through {@link HartshornApplication}.
     *
     * <p>In this example we wish to use the {@link CommandCLI} to be able to use the command line to enter commands.
     * An example command has been provided by {@link UserCommandService}.
     *
     * <p>Note the use of the generic type parameter {@link Started} in the event. This causes this method to
     * activate only when a {@link EngineChangedState} event is posted with this exact type parameter. When the
     * posted parameter is another sub-class of {@link ApplicationState} this method will not
     * activate. However, if the notation of this event changed to {@code EngineChangedState<?>} it would activate
     * with any type parameter, as long as the event itself is a {@link EngineChangedState}.
     */
    @Listener
    public void on(final EngineChangedState<Started> event) {
        event.applicationContext().get(CommandCLI.class).async(true).open();
    }

}
