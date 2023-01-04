/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.events.handle;

import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.component.condition.ProvidedParameterContext;
import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.events.EventWrapper;
import org.dockbox.hartshorn.events.parents.Event;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class ConditionMatcherEventExecutionFilter extends DefaultContext implements EventExecutionFilter {

    @Override
    public boolean accept(final Event event, final EventWrapper wrapper, final ComponentKey<?> target) {
        final ConditionMatcher matcher = event.applicationContext().get(ConditionMatcher.class);
        return matcher.match(wrapper.method(), this.matcherContexts(event, wrapper));
    }

    private Context[] matcherContexts(final Event event, final EventWrapper wrapper) {
        final Set<Context> contexts = new HashSet<>(this.unnamedContexts());
        if (this.first(ProvidedParameterContext.class).absent()) {
            if (wrapper.method().parameters().count() != 1) {
                throw new IllegalArgumentException("Method " + wrapper.method() + " has " + wrapper.method().parameters().count() + " parameters, but only one is allowed");
            }
            final ProvidedParameterContext parameterContext = ProvidedParameterContext.of(wrapper.method(), Collections.singletonList(event));
            contexts.add(parameterContext);
        }
        return contexts.toArray(new Context[0]);
    }
}
