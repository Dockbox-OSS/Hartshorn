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

package org.dockbox.hartshorn.demo.persistence.events;

import org.dockbox.hartshorn.demo.persistence.domain.User;
import org.dockbox.hartshorn.demo.persistence.services.UserRepository;
import org.dockbox.hartshorn.events.parents.ContextCarrierEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A simple custom event which is posted by {@link UserRepository}
 * after a {@link User} has been created through {@link UserRepository#save(User)}.
 *
 * <p>The {@link User#id() ID of the user} will be present when this event is posted.
 *
 * <p>Events which extend {@link ContextCarrierEvent} are enhanced with the active {@link org.dockbox.hartshorn.core.context.ApplicationContext}
 * so you are able to obtain the active context from it directly using {@link ContextCarrierEvent#applicationContext()} as demonstrated
 * by {@link org.dockbox.hartshorn.demo.persistence.services.EventListenerService#on(UserCreatedEvent)}.
 */
@Getter
@AllArgsConstructor
public class UserCreatedEvent extends ContextCarrierEvent {
    private User user;
}
