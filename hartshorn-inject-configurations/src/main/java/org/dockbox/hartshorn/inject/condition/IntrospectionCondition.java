/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.inject.condition;

/**
 * A condition that requires introspection of the annotated element being evaluated. This condition
 * receives an {@link IntrospectedConditionContext} when being matched. As a result, all tested
 * views will have been introspected, and therefore will already be loaded on the classpath.
 *
 * @see ConditionMatcher
 * @see Condition
 * 
 * @since 0.7.0
 * 
 * @author Guus Lieben
 */
@FunctionalInterface
public non-sealed interface IntrospectionCondition extends Condition {

    /**
     * Returns a {@link ConditionResult} that describes whether the condition is matched.
     *
     * @param context the context in which the condition is matched
     *
     * @return a {@link ConditionResult} that describes whether the condition is matched
     */
    @Override
    default ConditionResult matches(ConditionContext context) {
        if (context instanceof IntrospectedConditionContext introspectedConditionContext) {
            return this.matches(introspectedConditionContext);
        }
        return ConditionResult.notMatched(
                "ConditionContext is not an instance of IntrospectedConditionContext"
        );
    }

    /**
     * Matches the condition against the given context.
     *
     * @param context the introspected condition context
     * @return the condition result
     */
    ConditionResult matches(IntrospectedConditionContext context);
}
