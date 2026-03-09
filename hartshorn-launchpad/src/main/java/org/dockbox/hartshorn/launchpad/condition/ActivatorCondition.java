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

package org.dockbox.hartshorn.launchpad.condition;

import org.dockbox.hartshorn.inject.condition.CacheableCondition;
import org.dockbox.hartshorn.inject.condition.ConditionResult;
import org.dockbox.hartshorn.inject.condition.IntrospectedConditionContext;
import org.dockbox.hartshorn.inject.condition.IntrospectionCondition;
import org.dockbox.hartshorn.launchpad.ConfigurableActivationInjectionCapableApplication;
import org.dockbox.hartshorn.launchpad.activation.ModuleActivatorHolder;

import java.lang.annotation.Annotation;

/**
 * A condition that matches when an activator is present.
 *
 * @see RequiresActivator
 * @see ModuleActivatorHolder#hasActivator(Class)
 * 
 * @since 0.4.12
 * 
 * @author Guus Lieben
 */
public class ActivatorCondition implements IntrospectionCondition, CacheableCondition {

    @Override
    public ConditionResult matches(IntrospectedConditionContext context) {
        if (!(context.application() instanceof ConfigurableActivationInjectionCapableApplication
            configurableInjectionCapableApplication)) {
            return ConditionResult.notMatched("Application is not compatible with activators");
        }
        return context.annotatedElement()
            .annotations()
            .get(RequiresActivator.class)
            .map(condition -> {
                for (Class<? extends Annotation> activator : condition.value()) {
                    if (!configurableInjectionCapableApplication.activators()
                        .hasActivator(activator)) {
                        return ConditionResult.notFound("Activator", activator.getName());
                    }
                }
                return ConditionResult.matched();
            })
            .orElse(ConditionResult.invalidCondition("activator"));
    }
}
