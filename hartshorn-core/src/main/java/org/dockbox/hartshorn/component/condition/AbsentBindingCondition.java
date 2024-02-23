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

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;

/**
 * A condition that matches when a binding is absent. This does not require an instance
 * of the binding to be present, but only that a binding is defined in a {@link BindingHierarchy}.
 *
 * @see RequiresAbsentBinding
 * @see BindingHierarchy
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class AbsentBindingCondition implements Condition {

    @Override
    public ConditionResult matches(ConditionContext context) {
        return context.annotatedElement().annotations().get(RequiresAbsentBinding.class).map(condition -> {
            ComponentKey<?> key = ComponentKey.of(condition.value(), condition.name());
            BindingHierarchy<?> hierarchy = context.applicationContext().hierarchy(key);
            return hierarchy.size() > 0
                    ? ConditionResult.matched()
                    : ConditionResult.notFound("Binding", String.valueOf(key));
        }).orElse(ConditionResult.invalidCondition("absent binding"));
    }
}
