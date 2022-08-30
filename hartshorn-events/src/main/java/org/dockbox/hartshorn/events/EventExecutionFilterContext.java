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
