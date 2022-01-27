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

package org.dockbox.hartshorn.demo.persistence.services;

import org.dockbox.hartshorn.demo.persistence.domain.User;
import org.dockbox.hartshorn.demo.persistence.events.UserCreatedEvent;

import org.dockbox.hartshorn.commands.CommandListener;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
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
     * <p>In this example we wish to use the {@link CommandListener} to be able to use the command line to enter commands.
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
        event.applicationContext().get(CommandListener.class).async(true).open();
    }

}
