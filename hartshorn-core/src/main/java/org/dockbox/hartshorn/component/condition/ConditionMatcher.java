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

package org.dockbox.hartshorn.component.condition;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;

import java.util.Set;

public class ConditionMatcher {

    private final ApplicationContext applicationContext;

    public ConditionMatcher(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public boolean match(final AnnotatedElementView annotatedElementContext, final Context... contexts) {
        final Set<RequiresCondition> conditions = annotatedElementContext.annotations().all(RequiresCondition.class);
        for (final RequiresCondition condition : conditions) {
            final Class<? extends Condition> conditionClass = condition.condition();

            final Condition conditionInstance = this.applicationContext.get(conditionClass);
            if (!this.match(annotatedElementContext, conditionInstance, condition, contexts)) {
                return false;
            }
        }
        return true;
    }

    public boolean match(final AnnotatedElementView element, final Condition condition, final RequiresCondition requiresCondition, final Context... contexts) {
        final ConditionContext context = new ConditionContext(this.applicationContext, element, requiresCondition);
        for (final Context child : contexts) context.add(child);
        final ConditionResult result = condition.matches(context);
        if (!result.matches() && requiresCondition.failOnNoMatch()) {
            throw new ConditionFailedException(condition, result);
        }
        return result.matches();
    }

}
