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

package org.dockbox.hartshorn.events;

import org.dockbox.hartshorn.component.processing.Provider;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.util.parameter.ParameterLoader;
import org.dockbox.hartshorn.events.annotations.UseEvents;
import org.dockbox.hartshorn.events.handle.EventParameterLoader;

import jakarta.inject.Singleton;

@Service(activators = UseEvents.class)
public class EventProviders {

    @Singleton
    @Provider
    public Class<? extends EventBus> eventBus() {
        return EventBusImpl.class;
    }

    @Provider("event_loader")
    public ParameterLoader eventParameterLoader() {
        return new EventParameterLoader();
    }
}
