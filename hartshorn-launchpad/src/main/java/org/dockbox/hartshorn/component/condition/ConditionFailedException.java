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

import org.dockbox.hartshorn.util.ApplicationRuntimeException;

/**
 * Thrown when a {@link Condition} does not match, and the {@link RequiresCondition} annotation is configured to fail
 * on a mismatch.
 *
 * @see RequiresCondition#failOnNoMatch()
 * @see RequiresCondition
 * @see Condition
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class ConditionFailedException extends ApplicationRuntimeException {

    public ConditionFailedException(Condition condition, ConditionResult result) {
        super("Condition failed (" + condition.getClass().getSimpleName() + ") with reason: " + result.message());
    }
}
