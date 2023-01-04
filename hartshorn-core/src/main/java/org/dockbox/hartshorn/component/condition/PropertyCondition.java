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

import org.dockbox.hartshorn.util.option.Option;

public class PropertyCondition implements Condition {

    @Override
    public ConditionResult matches(final ConditionContext context) {
        return context.annotatedElement().annotations().get(RequiresProperty.class).map(condition -> {
            final String name = condition.name();
            final Option<String> result = context.applicationContext().property(name);
            if (result.absent()) {
                if (condition.matchIfMissing()) {
                    return ConditionResult.matched();
                }
                return ConditionResult.notFound("property", name);
            }

            final String value = result.get();
            if (condition.matchIfMissing()) {
                return ConditionResult.found("property", name, value);
            }

            if (condition.withValue().isEmpty()) {
                return ConditionResult.matched();
            }
            return condition.withValue().equals(value) ? ConditionResult.matched() : ConditionResult.notEqual("property", condition.withValue(), value);
        }).orCompute(() -> ConditionResult.invalidCondition("property")).get();
    }
}
