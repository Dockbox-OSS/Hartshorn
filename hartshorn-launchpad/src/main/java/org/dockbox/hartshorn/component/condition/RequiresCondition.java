/*
 * Copyright 2019-2024 the original author or authors.
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A generic condition that requires a specific condition to be met. The condition is defined by the
 * {@link #condition()} attribute. If the condition is not met, the annotated element is not processed.
 *
 * <p>In most cases, it is recommended to create a custom annotation that extends this annotation, and
 * use that annotation instead. This allows for a more readable code base. A basic example is shown
 * below.
 *
 * <pre>{@code
 * @Extends(RequiresCondition.class)
 * @RequiresCondition(condition  = SampleCondition.class)
 * public @interface RequiresClass {
 *    ... additional attributes for your condition ...
 *    boolean failOnNoMatch() default false;
 * }
 * }</pre>
 *
 * @see RequiresClass
 * @see Condition
 * @see ConditionMatcher
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresCondition {

    /**
     * The condition that is required to be met. The condition should be a stateless
     * class that implements {@link Condition}.
     *
     * @return the condition that is required to be met
     */
    Class<? extends Condition> condition();

    /**
     * Whether to fail on no match. If set to {@code true}, the operation will fail if the condition
     * is not met. It remains up to the implementation of the condition to determine what constitutes
     * a match.
     *
     * @return whether to fail on no match
     */
    boolean failOnNoMatch() default false;
}
