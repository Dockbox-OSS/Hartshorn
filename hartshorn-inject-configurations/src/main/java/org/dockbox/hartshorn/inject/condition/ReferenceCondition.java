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
 * A {@link Condition} that matches based on a reference, without requiring the element to be
 * loaded. This has the benefit of being able to evaluate conditions without causing class loading
 * early in the application lifecycle. The drawback is that the condition implementation must work
 * with references with limited information (compared to loaded elements, which support full
 * introspection).
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public non-sealed interface ReferenceCondition extends Condition {

    /**
     * Returns a {@link ConditionResult} that describes whether the condition is matched.
     *
     * @param context the context in which the condition is matched
     *
     * @return a {@link ConditionResult} that describes whether the condition is matched
     */
    @Override
    default ConditionResult matches(ConditionContext context) {
        if (context instanceof ReferenceConditionContext referenceConditionContext) {
            return this.matches(referenceConditionContext);
        }
        return ConditionResult.notMatched(
                "ConditionContext is not an instance of ReferenceConditionContext"
        );
    }

    /**
     * Matches the condition against the given context.
     *
     * @param context the type reference condition context
     * @return the condition result
     */
    ConditionResult matches(ReferenceConditionContext context);
}
