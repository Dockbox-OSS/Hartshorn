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

import org.dockbox.hartshorn.context.AutoCreating;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.events.handle.EventExecutionFilter;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@AutoCreating
public class EventExecutionFilterContext extends DefaultContext {

    private final Set<EventExecutionFilter> executionFilters = ConcurrentHashMap.newKeySet();

    public boolean contains(final EventExecutionFilter o) {
        return executionFilters.contains(o);
    }

    public boolean add(final EventExecutionFilter eventExecutionFilter) {
        return executionFilters.add(eventExecutionFilter);
    }

    public boolean remove(final EventExecutionFilter o) {
        return executionFilters.remove(o);
    }

    public boolean addAll(final Collection<? extends EventExecutionFilter> c) {
        return executionFilters.addAll(c);
    }

    public Set<EventExecutionFilter> filters() {
        return Set.copyOf(this.executionFilters);
    }
}