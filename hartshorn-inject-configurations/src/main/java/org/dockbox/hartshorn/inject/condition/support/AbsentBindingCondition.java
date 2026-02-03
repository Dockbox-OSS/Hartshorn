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

package org.dockbox.hartshorn.inject.condition.support;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.condition.ConditionResult;
import org.dockbox.hartshorn.inject.condition.IntrospectedConditionContext;
import org.dockbox.hartshorn.inject.condition.IntrospectionCondition;
import org.dockbox.hartshorn.inject.graph.ConditionalDependencyContext;
import org.dockbox.hartshorn.inject.graph.ConditionalDependencyContextsHolder;

import java.util.List;
import java.util.stream.Stream;

/**
 * A condition that matches when a binding is absent. This does not require an instance of the
 * binding to be present, but only that a binding is defined in a {@link BindingHierarchy}.
 *
 * @see RequiresAbsentBinding
 * @see BindingHierarchy
 * 
 * @since 0.4.12
 * 
 * @author Guus Lieben
 */
public class AbsentBindingCondition implements IntrospectionCondition {

    @Override
    public ConditionResult matches(IntrospectedConditionContext context) {
        return context.annotatedElement()
            .annotations()
            .get(RequiresAbsentBinding.class)
            .map(condition -> {
                ComponentKey<?> key = ComponentKey.of(condition.value(), condition.name());
                BindingHierarchy<?> hierarchy =
                    context.application().defaultBinder().hierarchy(key);
                if (hierarchy.size() > 0) {
                    return ConditionResult.found("Binding", String.valueOf(key));
                }
                else {
                    List<ConditionalDependencyContext<?>> matchedContexts =
                        context.firstContext(ConditionalDependencyContextsHolder.class).stream()
                            .flatMap(contextsHolder -> matchedConditionalContextsExceptCurrent(
                                contextsHolder,
                                context,
                                key))
                            .toList();
                    if (!matchedContexts.isEmpty()) {
                        return ConditionResult.found("Binding", String.valueOf(key));
                    }
                }
                return ConditionResult.matched();
            })
            .orElse(ConditionResult.invalidCondition("absent binding"));
    }

    private static Stream<ConditionalDependencyContext<?>> matchedConditionalContextsExceptCurrent(
        ConditionalDependencyContextsHolder contextsHolder,
        IntrospectedConditionContext context,
        ComponentKey<?> key
    ) {
        return contextsHolder.conditionalDependencyContexts().get(key).stream()
            .filter(conditionalDependencyContext -> !conditionalDependencyContext
                .dependencyContext()
                .origin()
                .equals(context.annotatedElement())
            )
            .filter(conditionalDependencyContext -> conditionalDependencyContext
                .conditionsMatched()
                .test(contextsHolder)
            );
    }
}
