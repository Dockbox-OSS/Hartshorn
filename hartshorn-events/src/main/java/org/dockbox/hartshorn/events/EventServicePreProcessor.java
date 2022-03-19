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

import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.component.processing.AutomaticActivation;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ServicePreProcessor;
import org.dockbox.hartshorn.events.annotations.Listener;
import org.dockbox.hartshorn.events.annotations.UseEvents;

@AutomaticActivation
public class EventServicePreProcessor implements ServicePreProcessor<UseEvents> {
    @Override
    public boolean preconditions(final ApplicationContext context, final Key<?> key) {
        return !key.type().methods(Listener.class).isEmpty();
    }

    @Override
    public <T> void process(final ApplicationContext context, final Key<T> key) {
        context.get(EventBus.class).subscribe(key);
    }

    @Override
    public Class<UseEvents> activator() {
        return UseEvents.class;
    }
}
