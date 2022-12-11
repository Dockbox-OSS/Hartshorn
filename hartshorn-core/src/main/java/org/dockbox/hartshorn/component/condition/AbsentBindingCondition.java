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

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.inject.processing.ProviderContextList;

public class AbsentBindingCondition implements Condition {

    @Override
    public ConditionResult matches(final ConditionContext context) {
        final ProviderContextList listContext = context.applicationContext().first(ProviderContextList.class).orNull();
        return context.annotatedElement().annotations().get(RequiresAbsentBinding.class).map(condition -> {
            final ComponentKey<?> key = ComponentKey.of(condition.value(), condition.name());
            if (listContext != null && listContext.containsKey(key)) {
                return ConditionResult.matched();
            }
            else return ConditionResult.notFound("Binding", String.valueOf(key));
        }).orElse(ConditionResult.invalidCondition("absent binding"));
    }
}
