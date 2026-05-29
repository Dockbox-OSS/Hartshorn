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

package org.dockbox.hartshorn.launchpad.test;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.HierarchicalBinder;
import org.dockbox.hartshorn.inject.condition.ConditionResult;
import org.dockbox.hartshorn.inject.condition.IntrospectedConditionContext;
import org.dockbox.hartshorn.inject.condition.IntrospectionCondition;
import org.dockbox.hartshorn.launchpad.ApplicationContext;

/**
 * A condition that matches when the application context contains a binding for
 * {@link ApplicationTestManager}. This component is only ever present when an application is
 * bootstrapped through the Hartshorn Test Suite, and is the preferred way to verify whether the
 * application is being executed in a test environment.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class TestApplicationCondition implements IntrospectionCondition {

    @Override
    public ConditionResult matches(IntrospectedConditionContext context) {
        HierarchicalBinder binder = context.application().defaultBinder();
        BindingHierarchy<ApplicationTestManager> hierarchy = binder.hierarchy(
                ComponentKey.of(ApplicationTestManager.class)
        );
        if (hierarchy.size() != 0) {
            return ConditionResult.matched();
        }
        else {
            return ConditionResult.notFound("test binding", ApplicationContext.class.getName());
        }
    }
}
