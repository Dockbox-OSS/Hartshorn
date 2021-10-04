/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.demo.persistence.events;

import org.dockbox.hartshorn.demo.persistence.domain.User;
import org.dockbox.hartshorn.demo.persistence.services.UserPersistence;
import org.dockbox.hartshorn.events.parents.ContextCarrierEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A simple custom event which is posted by {@link UserPersistence}
 * after a {@link User} has been created through {@link UserPersistence#createUser(String, int)}.
 *
 * <p>The {@link User#id() ID of the user} will be present when this event is posted.
 *
 * <p>Events which extend {@link ContextCarrierEvent} are enhanced with the active {@link org.dockbox.hartshorn.di.context.ApplicationContext}
 * so you are able to obtain the active context from it directly using {@link ContextCarrierEvent#applicationContext()} as demonstrated
 * by {@link org.dockbox.hartshorn.demo.persistence.services.EventListenerService#on(UserCreatedEvent)}.
 */
@Getter
@AllArgsConstructor
public class UserCreatedEvent extends ContextCarrierEvent {
    private User user;
}
