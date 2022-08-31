package org.dockbox.hartshorn.events.handle;

import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.component.condition.ProvidedParameterContext;
import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.events.EventWrapper;
import org.dockbox.hartshorn.events.parents.Event;
import org.dockbox.hartshorn.inject.Key;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class ConditionMatcherEventExecutionFilter extends DefaultContext implements EventExecutionFilter {

    @Override
    public boolean accept(final Event event, final EventWrapper wrapper, final Key<?> target) {
        final ConditionMatcher matcher = event.applicationContext().get(ConditionMatcher.class);
        return matcher.match(wrapper.method(), this.matcherContexts(event, wrapper));
    }

    private Context[] matcherContexts(final Event event, final EventWrapper wrapper) {
        final Set<Context> contexts = new HashSet<>(this.contexts);
        if (this.first(ProvidedParameterContext.class).absent()) {
            if (wrapper.method().parameterCount() != 1) {
                throw new IllegalArgumentException("Method " + wrapper.method() + " has " + wrapper.method().parameterCount() + " parameters, but only one is allowed");
            }
            final ProvidedParameterContext parameterContext = ProvidedParameterContext.of(wrapper.method(), Collections.singletonList(event));
            contexts.add(parameterContext);
        }
        return contexts.toArray(new Context[0]);
    }
}
