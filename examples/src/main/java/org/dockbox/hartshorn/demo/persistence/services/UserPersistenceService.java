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

package org.dockbox.hartshorn.demo.persistence.services;

import org.dockbox.hartshorn.demo.persistence.domain.User;
import org.dockbox.hartshorn.demo.persistence.events.UserCreatedEvent;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.events.EventBus;
import org.dockbox.hartshorn.persistence.SqlService;

import javax.inject.Inject;

@Service
public class UserPersistenceService {

    @Inject
    private SqlService sqlService;
    @Inject
    private EventBus eventBus;

    public User createUser(String name, int age) {
        User user = new User(name, age);
        this.sqlService.save(user);
        this.eventBus.post(new UserCreatedEvent(user));
        return user;
    }

}
