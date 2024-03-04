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

import org.dockbox.hartshorn.profiles.ComposableProfileHolder;
import org.dockbox.hartshorn.profiles.ProfileProperty;
import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.ValueProfileProperty;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A condition that checks if a property is present in the application context. If the property is present and an expected
 * value is configured, the condition will check if the value matches the configured value. If the property is not present,
 * the condition will check if the {@link RequiresProperty#matchIfMissing()} is set to {@code true}.
 *
 * @see RequiresProperty
 * @see Condition
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class PropertyCondition implements Condition {

    @Override
    public ConditionResult matches(ConditionContext context) {
        return context.annotatedElement().annotations().get(RequiresProperty.class).map(condition -> {
            String name = condition.name();
            ComposableProfileHolder profiles = context.applicationContext().environment().profiles();
            ProfilePropertyRegistry propertyRegistry = profiles.registry();

            Option<ProfileProperty> result = propertyRegistry.property(name);
            if (result.absent()) {
                if (condition.matchIfMissing()) {
                    return ConditionResult.matched();
                }
                return ConditionResult.notFound("property", name);
            }

            String expectedValue = condition.withValue();
            if (expectedValue.isEmpty()) {
                return ConditionResult.matched();
            }

            ProfileProperty property = result.get();
            if (!(property instanceof ValueProfileProperty valueProfileProperty)) {
                return ConditionResult.notMatched("Expected a value property, but found a different type");
            }

            Option<String> actualValue = valueProfileProperty.rawValue();
            if (actualValue.absent()) {
                return ConditionResult.notEqual("property", "not empty", name);
            }

            String value = actualValue.get();
            return expectedValue.equals(value)
                    ? ConditionResult.matched()
                    : ConditionResult.notEqual("property", expectedValue, value);
        }).orCompute(() -> ConditionResult.invalidCondition("property")).get();
    }
}
