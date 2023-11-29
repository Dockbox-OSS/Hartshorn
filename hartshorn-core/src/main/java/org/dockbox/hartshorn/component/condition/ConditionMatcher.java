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

package org.dockbox.hartshorn.component.condition;

import java.util.Set;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;

/**
 * A matcher that can be used to match {@link RequiresCondition} annotations against a given set of contexts.
 * This matcher will use the {@link ApplicationContext} to resolve the {@link Condition} instances that are
 * referenced by the {@link RequiresCondition} annotations.
 *
 * <p>Condition matching can be used for a variety of purposes. For example, it can be used to determine whether
 * a component should be registered, a binding method should be invoked, or an event should be dispatched.
 *
 * @see RequiresCondition
 * @see Condition
 * @see ConditionContext
 * @see ConditionResult
 *
 * @param applicationContext the application context, used to resolve {@link Condition} instances
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public record ConditionMatcher(ApplicationContext applicationContext) implements ContextCarrier {

    /**
     * Matches the {@link RequiresCondition} annotations of the given {@link AnnotatedElementView}, providing
     * any additional {@link Context} instances to the {@link ConditionContext} that is used to match the
     * {@link Condition condition implementations}. If any of the conditions does not match, this method will
     * return {@code false}. If all conditions match, this method will return {@code true}.
     *
     * @param annotatedElementContext the annotated element to match against
     * @param contexts the additional contexts to provide to the condition context
     *
     * @return {@code true} if all conditions match, {@code false} otherwise
     */
    public boolean match(AnnotatedElementView annotatedElementContext, Context... contexts) {
        Set<RequiresCondition> conditions = annotatedElementContext.annotations().all(RequiresCondition.class);
        for (RequiresCondition condition : conditions) {
            Class<? extends Condition> conditionClass = condition.condition();

            Condition conditionInstance = this.applicationContext.get(conditionClass);
            if (!this.match(annotatedElementContext, conditionInstance, condition, contexts)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Matches the given {@link Condition} against the given {@link AnnotatedElementView}, providing any additional
     * {@link Context} instances to the {@link ConditionContext} that is used to match the {@link Condition condition
     * implementations}. If the condition does not match, this method will return {@code false}. If the condition
     * matches, this method will return {@code true}.
     *
     * @param element the annotated element to match against
     * @param condition the condition to match
     * @param requiresCondition the {@link RequiresCondition} annotation that references the condition
     * @param contexts the additional contexts to provide to the condition context
     *
     * @return {@code true} if the condition matches, {@code false} otherwise
     */
    public boolean match(AnnotatedElementView element, Condition condition, RequiresCondition requiresCondition, Context... contexts) {
        ConditionContext context = new ConditionContext(this.applicationContext, element, requiresCondition);
        for (Context child : contexts) {
            context.add(child);
        }
        ConditionResult result = condition.matches(context);
        if (!result.matches() && requiresCondition.failOnNoMatch()) {
            throw new ConditionFailedException(condition, result);
        }
        return result.matches();
    }
}
