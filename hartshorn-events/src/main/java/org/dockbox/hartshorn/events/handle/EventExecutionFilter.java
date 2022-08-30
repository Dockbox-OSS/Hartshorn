package org.dockbox.hartshorn.events.handle;

import org.dockbox.hartshorn.events.EventWrapper;
import org.dockbox.hartshorn.events.parents.Event;
import org.dockbox.hartshorn.inject.Key;

public interface EventExecutionFilter {
    boolean accept(Event event, EventWrapper wrapper, Key<?> target);
}
